package de.moyapro.nushppinglist.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.getCartItemByItemId
import de.moyapro.nushppinglist.db.dao.getItemByItemId
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.db.model.*
import de.moyapro.nushppinglist.mock.CartDaoMock
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect

@FlowPreview
class CartViewModel(
    private val cartDao: CartDao,
) : ViewModel() {

    constructor() : this(CartDaoMock(CoroutineScope(Dispatchers.IO + SupervisorJob())))

    private val _cartItems = MutableStateFlow<List<CartItemProperties>>(emptyList())
    val cartItems: StateFlow<List<CartItemProperties>> = _cartItems
    private val _nonCartItems = MutableStateFlow<List<Item>>(emptyList())
    val nonCartItems: StateFlow<List<Item>> = _nonCartItems
    private val _allItems = MutableStateFlow<List<Item>>(emptyList())
    val allItems: StateFlow<List<Item>> = _allItems
    private val _allCartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val allCartItems: StateFlow<List<CartItem>> = _allCartItems
    private val _allCartItemsGrouped = MutableStateFlow<Map<RecipeId?, List<CartItem>>>(emptyMap())
    val allCartItemsGrouped: StateFlow<Map<RecipeId?, List<CartItem>>> = _allCartItemsGrouped

    init {
        _cartItems.listenTo(cartDao.findAllInCart(), viewModelScope)
        _allItems.listenTo(cartDao.findAllItems(), viewModelScope)
        _allCartItems.listenTo(cartDao.findAllCartItems(), viewModelScope)
        _allCartItemsGrouped.listenTo(cartDao.findAllCartItems(),
            viewModelScope,
            ModelTransformation::groupCartItemsByRecipe)

        viewModelScope.launch {
            cartDao.findAllItems()
                .collect { items ->
                    val cartItemIds = _cartItems.value.map { cartItem -> cartItem.itemId }
                    _nonCartItems.value =
                        items.filter { item -> !cartItemIds.contains(item.itemId) }
                }
        }
    }

    fun add(newItem: Item) = viewModelScope.launch(Dispatchers.IO) {
        cartDao.save(newItem)
    }

    fun update(updatedItem: Item) = viewModelScope.launch(Dispatchers.IO) {
        cartDao.updateAll(updatedItem)
    }

    fun update(updatedCartItemProperties: CartItemProperties) = viewModelScope.launch(Dispatchers.IO) {
        cartDao.updateAll(updatedCartItemProperties)
    }

    @Transaction
    fun add(newItem: CartItem) = viewModelScope.launch(Dispatchers.IO) {
        if (allItems.value.map { it.itemId }.contains(newItem.item.itemId)) {
            cartDao.updateAll(newItem.item)
        } else {
            cartDao.save(newItem.item)
        }
        if (allCartItems.value.map { it.cartItemProperties.cartItemPropertiesId }
                .contains(newItem.cartItemProperties.cartItemPropertiesId)) {
            cartDao.updateAll(newItem.cartItemProperties)
        } else {
            cartDao.save(newItem.cartItemProperties)
        }
    }

    fun toggleChecked(itemToToggle: CartItemProperties) = viewModelScope.launch(Dispatchers.IO) {
        _cartItems.value = _cartItems.value.map { oldValue ->
            if (oldValue.itemId == itemToToggle.itemId) {
                val updated = oldValue.copy(
                    checked = !oldValue.checked
                )
                cartDao.updateAll(updated)
                updated
            } else {
                oldValue
            }
        }
    }

    fun getItemByItemId(itemId: ItemId): Item? = runBlocking {
        cartDao.getItemByItemId(itemId)
    }

    fun getAutocompleteItems(searchString: String): List<String> {
        return allItems.value
            .filter { matched(it.name, searchString) }
            .map { it.name }
    }

    fun addToCart(item: Item) = viewModelScope.launch(Dispatchers.IO) {
        val existingCartItem: CartItemProperties? =
            cartDao.getCartItemByItemId(item.itemId)
        if (null == existingCartItem) {
            add(CartItem(item))
        } else {
            val updatedCartItemProperties =
                existingCartItem.copy(amount = existingCartItem.amount + 1)
            update(updatedCartItemProperties)
        }
    }

    fun addToCart(recipeItems: List<RecipeItem>) {
        recipeItems.forEach(this::addToCart)
    }

    fun addToCart(recipeItem: RecipeItem) = viewModelScope.launch(Dispatchers.IO) {
        val existingCartItem: CartItemProperties? =
            cartDao.getCartItemByItemId(recipeItem.item.itemId)
        if (null == existingCartItem) {
            val newItem = CartItem(recipeItem.item)
            newItem.cartItemProperties.recipeId = recipeItem.recipeId
            add(newItem)
        } else {
            val updatedCartItemProperties =
                existingCartItem.copy(amount = existingCartItem.amount + 1)
            update(updatedCartItemProperties)
        }
    }


    fun addToCart(itemName: String) = viewModelScope.launch(Dispatchers.IO) {
        val existingItem: Item? = cartDao.getItemByItemName(itemName)
        if (null == existingItem) {
            add(CartItem(itemName))
        } else {
            val existingCartItem = cartDao.getCartItemByItemId(existingItem.itemId)
            if (null == existingCartItem) {
                cartDao.save(CartItem(existingItem).cartItemProperties)
            } else {
                cartDao.updateAll(existingCartItem.copy(amount = existingCartItem.amount + 1))
            }
        }
    }

    fun removeCheckedFromCart() = viewModelScope.launch(Dispatchers.IO) {
        cartItems.value
            .filter { it.checked }
            .forEach { cartItemProperties ->
                cartDao.remove(cartItemProperties)
            }
    }

    fun getCartItemPropertiesByItemId(itemId: ItemId): CartItemProperties? = runBlocking {
        cartDao.getCartItemByItemId(itemId)
    }

    fun addRecipeToCart(recipe: Recipe) {
        recipe.recipeItems.forEach(::addToCart)
    }

    fun subtractFromCart(itemId: ItemId) = viewModelScope.launch(Dispatchers.IO) {
        val cartItem = cartDao.getCartItemByItemId(itemId)
        when {
            cartItem == null -> {}
            cartItem.amount <= 1 -> cartDao.remove(cartItem)
            cartItem.amount > 1 -> cartDao.updateAll(cartItem.copy(amount = cartItem.amount - 1))
            else -> {}
        }
    }

    fun removeItem(itemToRemove: Item) = viewModelScope.launch(Dispatchers.IO) {
        cartDao.remove(itemToRemove)
        val cartItemProperties = cartDao.getCartItemByItemId(itemToRemove.itemId)
        if (null != cartItemProperties) {
            cartDao.remove(cartItemProperties)
        }
    }
}

fun matched(name: String, searchString: String): Boolean {
    return name.contains(searchString, true)
}

