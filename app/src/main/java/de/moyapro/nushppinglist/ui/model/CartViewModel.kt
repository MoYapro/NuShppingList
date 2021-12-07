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
import kotlinx.coroutines.flow.Flow
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

    private fun <T> MutableStateFlow<T>.listenTo(source: Flow<T>) {
        return this.listenTo(source) { x -> x }
    }

    private fun <T, R> MutableStateFlow<R>.listenTo(source: Flow<T>, transformation: ((T) -> R)) {
        val that = this
        viewModelScope.launch {
            source.collect { valuesFromSource ->
                that.value = transformation(valuesFromSource)
            }
        }
    }

    init {
        _cartItems.listenTo(cartDao.findAllInCart())
        _allItems.listenTo(cartDao.findAllItems())
        _allCartItems.listenTo(cartDao.findAllCartItems())
        _allCartItemsGrouped.listenTo(cartDao.findAllCartItems(), ModelTransformation::groupCartItemsByRecipe)

        viewModelScope.launch {
            cartDao.findAllItems()
                .collect { items ->
                    val cartItemIds = _cartItems.value.map { cartItem -> cartItem.itemId }
                    _nonCartItems.value =
                        items.filter { item -> !cartItemIds.contains(item.itemId) }
                }
        }
    }

    fun add(newItem: Item) {
        cartDao.save(newItem)
    }

    fun update(updatedItem: Item) {
        cartDao.updateAll(updatedItem)
    }

    fun update(updatedCartItemProperties: CartItemProperties) {
        cartDao.updateAll(updatedCartItemProperties)
    }

    @Transaction
    fun add(newItem: CartItem) {
        cartDao.save(newItem.item)
        cartDao.save(newItem.cartItemProperties)
    }

    fun toggleChecked(itemToToggle: CartItemProperties) {
        _cartItems.value = _cartItems.value.map { oldValue ->
            if (oldValue.itemId == itemToToggle.itemId) {
                val updated = oldValue.copy(
                    checked = !oldValue.checked
                )
                this.cartDao.updateAll(updated)
                updated
            } else {
                oldValue
            }
        }
    }

    fun getItemByItemId(itemId: ItemId): Item? {
        return cartDao.getItemByItemId(itemId)
    }

    fun getAutocompleteItems(searchString: String): List<String> {
        return nonCartItems.value
            .filter { matched(it.name, searchString) }
            .map { it.name }
    }

    fun addToCart(item: Item) {
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

    fun addToCart(recipeItem: RecipeItem) {
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


    fun addToCart(itemName: String) {
        val existingItem: Item? = cartDao.getItemByItemName(itemName)
        if (null == existingItem) {
            add(CartItem(itemName))
        } else {
            cartDao.save(CartItem(existingItem).cartItemProperties)
        }
    }

    fun removeCheckedFromCart() {
        cartItems.value
            .filter { it.checked }
            .forEach { cartItem ->
                cartDao.remove(cartItem)
            }
    }

    fun getCartItemPropertiesByItemId(itemId: ItemId): CartItemProperties? {
        return cartDao.getCartItemByItemId(itemId)
    }

    fun addRecipeToCart(recipe: Recipe) {
        recipe.recipeItems.forEach(::addToCart)
    }


}

fun matched(name: String, searchString: String): Boolean {
    return name.contains(searchString, true)
}

