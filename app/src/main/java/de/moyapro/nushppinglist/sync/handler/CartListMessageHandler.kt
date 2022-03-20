package de.moyapro.nushppinglist.sync.handler

import android.util.Log
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.getCartByCartId
import de.moyapro.nushppinglist.db.model.Cart
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.CartListMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage
import de.moyapro.nushppinglist.ui.util.forEach

class CartListMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher,
) : suspend (CartListMessage) -> Unit {

    private val tag = CartListMessageHandler::class.simpleName

    override suspend fun invoke(cartlistMessage: CartListMessage) {
        cartlistMessage.carts.forEach(::handleCart)
    }

    private suspend fun handleCart(cart: Cart) {
        val existingCart: Cart? = cartDao.getCartByCartId(cart.cartId)
        Log.d(tag, "vvv\tCart: $cart")
        when {
            cart == existingCart -> return
            null == existingCart -> {
                cartDao.save(cart)
                publisher.publish(RequestCartMessage(cart.cartId))
            }
            else -> {
                cartDao.updateAll(cart)
            }
        }
    }
}
