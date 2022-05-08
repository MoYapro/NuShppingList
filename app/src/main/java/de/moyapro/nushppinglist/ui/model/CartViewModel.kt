package de.moyapro.nushppinglist.ui.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import de.moyapro.nushppinglist.constants.CONSTANTS.DEFAULT_CART
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.findAllSelectedCartItems
import de.moyapro.nushppinglist.db.dao.getCartItemByItemId
import de.moyapro.nushppinglist.db.dao.getItemByItemId
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.db.model.*
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.handler.CartMessageHandler
import de.moyapro.nushppinglist.sync.handler.ItemMessageHandler
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.sync.messages.ItemMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartListMessage
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@FlowPreview
class CartViewModel(
    private val cartDao: CartDao,
    private val publisher: Publisher? = null,
) : ViewModel() {

    private val itemMessageHandler = ItemMessageHandler(cartDao, publisher)
    private val cartMessageHandler = CartMessageHandler(cartDao, publisher)

    private val tag = CartViewModel::class.simpleName

    constructor() : this(CartDaoMock(CoroutineScope(Dispatchers.IO + SupervisorJob())))

    private var _selectedCart = MutableStateFlow(DEFAULT_CART)
    val selectedCart: MutableStateFlow<Cart> = _selectedCart

    private val _cartItems = MutableStateFlow<List<CartItemProperties>>(emptyList())
    val cartItems: StateFlow<List<CartItemProperties>> = _cartItems
    private val _allItems = MutableStateFlow<List<Item>>(emptyList())
    val allItems: StateFlow<List<Item>> = _allItems
    private val _allCartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val allCartItems: StateFlow<List<CartItem>> = _allCartItems
    private var _currentCartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val currentCartItems = _currentCartItems
    var job1: Job? = null
    var job2: Job? = null
    var job3: Job? = null
    var job4: Job? = null
    var job5: Job? = null
    var job6: Job? = null
    var job7: Job? = null
    private val _allCartItemsGrouped = MutableStateFlow<Map<RecipeId?, List<CartItem>>>(emptyMap())
    val allCartItemsGrouped: StateFlow<Map<RecipeId?, List<CartItem>>> = _allCartItemsGrouped
    private val _allCart = MutableStateFlow<List<Cart>>(emptyList())
    val allCart: StateFlow<List<Cart>> = _allCart

    init {
        job1?.cancel()
        job2?.cancel()
        job3?.cancel()
        job4?.cancel()
        job5?.cancel()
        job2 = _cartItems.listenTo(cartDao.findAllInCart(), viewModelScope)
        job3 = _allItems.listenTo(cartDao.findAllItems(), viewModelScope)
        job4 = _allCartItems.listenTo(cartDao.findAllCartItems(), viewModelScope)
        job5 = _allCart.listenTo(cartDao.findAllCart(), viewModelScope)
        job6 = _selectedCart.listenTo(cartDao.findSelectedCart(), viewModelScope)

    }

    private suspend fun updateSelectedCart(cart: Cart?) {
        cartDao.selectCart(cart?.cartId?.id)
        job1?.cancel()
        job2?.cancel()
        job1 =
            _currentCartItems.listenTo(cartDao.findAllSelectedCartItems(_selectedCart.value.cartId),
                viewModelScope)
        job2 = _allCartItemsGrouped.listenTo(
            cartDao.findAllSelectedCartItems(_selectedCart.value.cartId),
            viewModelScope,
            ModelTransformation::groupCartItemsByRecipe
        )
    }

    fun add(newItem: Item) = viewModelScope.launch(Dispatchers.IO) {
        publish(newItem)
        Log.d(tag, "+++\tItem\t $newItem")
        itemMessageHandler(ItemMessage(newItem))
    }

    fun add(newCart: Cart) = viewModelScope.launch(Dispatchers.IO) {
        Log.d(tag, "+++\tCart\t $newCart")
        cartDao.save(newCart)
    }


    fun update(updatedItem: Item) = viewModelScope.launch(Dispatchers.IO) {
        publish(updatedItem)
        itemMessageHandler(ItemMessage(updatedItem))
    }

    fun update(updatedCartItemProperties: CartItemProperties) =
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(tag, "vvv\t$updatedCartItemProperties")
            cartDao.updateAll(updatedCartItemProperties)
            cartMessageHandler(CartMessage(listOf(updatedCartItemProperties)))
        }

    fun update(updatedCart: Cart) = viewModelScope.launch(Dispatchers.IO) {
        Log.d(tag, "vvv\tCart: $updatedCart")
        cartDao.updateAll(updatedCart)
    }

    @Transaction
    fun add(newCartItem: CartItem) = viewModelScope.launch(Dispatchers.IO) {
        publish(newCartItem.item)
        publish(newCartItem.cartItemProperties)
        Log.d(tag, "+++\tCartItem\t $newCartItem")
        cartMessageHandler(
            CartMessage(
                cartItemPropertiesList = listOf(newCartItem.cartItemProperties),
                itemList = listOf(newCartItem.item),
                cartId = newCartItem.cartItemProperties.inCart
            )
        )
    }

    fun toggleChecked(itemToToggle: CartItemProperties) = viewModelScope.launch(Dispatchers.IO) {
        itemToToggle.checked = !itemToToggle.checked
        publish(itemToToggle)
        cartMessageHandler(CartMessage(listOf(itemToToggle)))
    }

    fun getItemByItemId(itemId: ItemId): Item? = runBlocking {
        Log.d(tag, "^^^\tItem by $itemId")
        cartDao.getItemByItemId(itemId)
    }

    fun getAutocompleteItems(searchString: String): List<String> {
        return allItems.value
            .filter { matched(it.name, searchString) }
            .map { it.name }
    }

    fun addToCart(item: Item) = viewModelScope.launch(Dispatchers.IO) {
        val existingCartItem: CartItemProperties? =
            cartDao.getCartItemByItemId(item.itemId, _selectedCart.value.cartId)
        if (null == existingCartItem) {
            add(CartItem(item, _selectedCart.value.cartId))
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
        Log.d(tag, "+++\tRecipeItem\t $recipeItem")
        val existingCartItem: CartItemProperties? =
            cartDao.getCartItemByItemId(recipeItem.item.itemId, _selectedCart.value.cartId)
        if (null == existingCartItem) {
            val newItem = CartItem(recipeItem.item, _selectedCart.value.cartId)
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
            Log.d(tag, "create new item from name: $itemName")
            add(CartItem(itemName, _selectedCart.value.cartId))
        } else {
            Log.d(tag, "add item with name $itemName to cart ${_selectedCart.value.cartId}")
            val existingCartItem =
                cartDao.getCartItemByItemId(existingItem.itemId, _selectedCart.value.cartId)
            if (null == existingCartItem) {
                cartMessageHandler(CartMessage(listOf(CartItem(existingItem,
                    _selectedCart.value.cartId).cartItemProperties)))
            } else {
                cartMessageHandler(CartMessage(listOf(existingCartItem.copy(amount = existingCartItem.amount + 1))))
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
        Log.d(tag, "^^^\tItem by $itemId")
        cartDao.getCartItemByItemId(itemId, _selectedCart.value.cartId)
    }

    fun addRecipeToCart(recipe: Recipe) {
        recipe.recipeItems.forEach(::addToCart)
    }

    fun subtractFromCart(itemId: ItemId) = viewModelScope.launch(Dispatchers.IO) {
        val updatedCartItemProperties =
            cartDao.getCartItemByItemId(itemId, _selectedCart.value.cartId)?.apply {
                amount -= 1
            }
        if (null != updatedCartItemProperties) {
            update(updatedCartItemProperties)
        }
    }

    fun removeItem(itemToRemove: Item) = viewModelScope.launch(Dispatchers.IO) {
        cartDao.remove(itemToRemove)
        val cartItemProperties =
            cartDao.getCartItemByItemId(itemToRemove.itemId, _selectedCart.value.cartId)
        if (null != cartItemProperties) {
            cartDao.remove(cartItemProperties)
        }
    }

    fun removeCart(cartToRemove: Cart) = viewModelScope.launch(Dispatchers.IO) {
        if(cartToRemove.cartId != DEFAULT_CART.cartId)
        cartDao.remove(cartToRemove)
    }

    fun selectCart(toBeSelected: Cart?) = runBlocking {
        Log.d(tag, "vvv\tselect Cart: $toBeSelected")
        cartDao.selectCart(toBeSelected?.cartId?.id)
        updateSelectedCart(toBeSelected)
    }

    fun publish(item: Item) {
        publisher?.publish(ItemMessage(item))
    }

    fun requestCartList() {
        publisher?.publish(RequestCartListMessage())
    }

    fun publish(cartItemProperties: CartItemProperties) {
        if (_selectedCart.value?.synced == true || null != _selectedCart.value.cartId) {
            val cartMessage = CartMessage(listOf(cartItemProperties), _selectedCart.value!!.cartId)
            publisher?.publish(cartMessage)
        }
    }
}

fun matched(name: String, searchString: String): Boolean {
    return name.contains(searchString, true)
}

