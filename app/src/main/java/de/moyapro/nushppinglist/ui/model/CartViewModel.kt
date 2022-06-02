package de.moyapro.nushppinglist.ui.model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Transaction
import de.moyapro.nushppinglist.constants.CONSTANTS.DEFAULT_CART
import de.moyapro.nushppinglist.db.dao.*
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
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@FlowPreview
class CartViewModel(
    private val cartDao: CartDao,
    private val publisher: Publisher? = null,
) : ViewModel() {

    private val itemMessageHandler = ItemMessageHandler(cartDao, publisher)
    private val cartMessageHandler = CartMessageHandler(cartDao, publisher)

    private val tag = CartViewModel::class.simpleName

    constructor() : this(CartDaoMock(CoroutineScope(Dispatchers.IO + SupervisorJob())))

    // might not have been initialized
    private var _selectedCart: MutableStateFlow<Cart?> = MutableStateFlow(DEFAULT_CART)
    val selectedCart: MutableStateFlow<Cart?> = _selectedCart

    private val _cartItems = MutableStateFlow<List<CartItemProperties>>(emptyList())
    val cartItems: StateFlow<List<CartItemProperties>> = _cartItems
    private val _allItems = MutableStateFlow<List<Item>>(emptyList())
    val allItems: StateFlow<List<Item>> = _allItems
    private val _allCartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val allCartItems: StateFlow<List<CartItem>> = _allCartItems
    private var _currentCartItems = MutableStateFlow<List<CartItem>>(emptyList())
    private var currentCartItemsJob1: Job? = null
    private var findAllInCartJob2: Job? = null
    private var findAllItemsJob3: Job? = null
    private var findAllCartItemsJob4: Job? = null
    private var findAllCartJob5: Job? = null
    private var findSelectedCartJob6: Job? = null
    private var findAllSelectedCartItemsJob7: Job? = null
    private val _allCartItemsGrouped = MutableStateFlow<Map<RecipeId?, List<CartItem>>>(emptyMap())


    @Deprecated("Just filter it youself")
    val allCartItemsGrouped: StateFlow<Map<RecipeId?, List<CartItem>>> = _allCartItemsGrouped
    private val _allCart = MutableStateFlow<List<Cart>>(emptyList())
    val allCart: StateFlow<List<Cart>> = _allCart

    init {
        currentCartItemsJob1?.cancel()
        findAllInCartJob2?.cancel()
        findAllItemsJob3?.cancel()
        findAllCartItemsJob4?.cancel()
        findAllCartJob5?.cancel()
        findSelectedCartJob6?.cancel()
        findAllInCartJob2 = _cartItems.listenTo(cartDao.findAllInCart(), viewModelScope)
        findAllItemsJob3 = _allItems.listenTo(cartDao.findAllItems(), viewModelScope)
        findAllCartItemsJob4 = _allCartItems.listenTo(cartDao.findAllCartItems(), viewModelScope)
        findAllCartJob5 = _allCart.listenTo(cartDao.findAllCart(), viewModelScope)
        findSelectedCartJob6 = _selectedCart.listenTo(cartDao.findSelectedCart(), viewModelScope)

    }

    private suspend fun updateSelectedCart(cart: Cart?) {
        cartDao.selectCart(cart?.cartId?.id)
        currentCartItemsJob1?.cancel()
        findAllSelectedCartItemsJob7?.cancel()
        currentCartItemsJob1 =
            _currentCartItems.listenTo(cartDao.findAllSelectedCartItems(getSelectedCart().cartId),
                viewModelScope)
        findAllSelectedCartItemsJob7 = _allCartItemsGrouped.listenTo(
            cartDao.findAllSelectedCartItems(getSelectedCart().cartId),
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
        val shouldSelectNewCart = newCart.selected
        newCart.selected = false // inserting selected would insert a second selected cart
        cartDao.save(newCart)
        if (shouldSelectNewCart) {
            selectCart(newCart)
        }
    }


    fun update(updatedItem: Item) = viewModelScope.launch(Dispatchers.IO) {
        publish(updatedItem)
        itemMessageHandler(ItemMessage(updatedItem))
    }

    fun update(updatedCartItemProperties: CartItemProperties) =
        viewModelScope.launch(Dispatchers.IO) {
            Log.d(tag, "vvv\t$updatedCartItemProperties")
            cartDao.updateAll(updatedCartItemProperties)
            cartMessageHandler(CartMessage(listOf(updatedCartItemProperties),
                updatedCartItemProperties.inCart))
        }

    fun update(updatedCart: Cart) = viewModelScope.launch(Dispatchers.IO) {
        Log.d(tag, "vvv\tCart: $updatedCart")
        val shouldBeSelected = updatedCart.selected
        cartDao.updateAll(updatedCart.copy(selected = false))
        if (shouldBeSelected) {
            selectCart(updatedCart)
        }
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
        cartMessageHandler(CartMessage(listOf(itemToToggle), itemToToggle.inCart))
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
            cartDao.getCartItemByItemId(item.itemId, getSelectedCart().cartId)
        if (null == existingCartItem) {
            add(CartItem(item, getSelectedCart().cartId))
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
            cartDao.getCartItemByItemId(recipeItem.item.itemId, getSelectedCart().cartId)
        if (null == existingCartItem) {
            val newItem = CartItem(recipeItem.item, getSelectedCart().cartId)
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
        val selectedCartId = getSelectedCart().cartId
        if (null == existingItem) {
            Log.d(tag, "create new item from name: $itemName")
            add(CartItem(itemName, selectedCartId))
        } else {
            Log.d(tag, "add item with name $itemName to cart $selectedCartId")
            val existingCartItem =
                cartDao.getCartItemByItemId(existingItem.itemId, selectedCartId)
            if (null == existingCartItem) {
                cartMessageHandler(CartMessage(listOf(CartItem(existingItem,
                    selectedCartId).cartItemProperties), selectedCartId))
            } else {
                cartMessageHandler(CartMessage(listOf(existingCartItem.copy(amount = existingCartItem.amount + 1)),
                    existingCartItem.inCart))
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
        cartDao.getCartItemByItemId(itemId, getSelectedCart().cartId)
    }

    fun addRecipeToCart(recipe: Recipe) {
        recipe.recipeItems.forEach(::addToCart)
    }

    fun subtractFromCart(itemId: ItemId) = viewModelScope.launch(Dispatchers.IO) {
        val updatedCartItemProperties =
            cartDao.getCartItemByItemId(itemId, getSelectedCart().cartId)?.apply {
                amount -= 1
            }
        if (null != updatedCartItemProperties) {
            update(updatedCartItemProperties)
        }
    }

    fun removeItem(itemToRemove: Item) = viewModelScope.launch(Dispatchers.IO) {
        cartDao.remove(itemToRemove)
        cartDao.removeCartItemPropertiesByItemId(itemToRemove.itemId)
    }

    fun removeCart(cartToRemove: Cart) = viewModelScope.launch(Dispatchers.IO) {
        if (cartToRemove.cartId != DEFAULT_CART.cartId)
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
        if (getSelectedCart().synced) {
            val cartMessage = CartMessage(listOf(cartItemProperties), getSelectedCart()!!.cartId)
            publisher?.publish(cartMessage)
        }
    }

    private fun getSelectedCart(): Cart = runBlocking {
        val selectedCart = cartDao.getSelectedCart()
        _selectedCart.value = selectedCart
        return@runBlocking selectedCart ?: DEFAULT_CART
    }
}

fun matched(name: String, searchString: String): Boolean {
    return name.contains(searchString, true)
}

