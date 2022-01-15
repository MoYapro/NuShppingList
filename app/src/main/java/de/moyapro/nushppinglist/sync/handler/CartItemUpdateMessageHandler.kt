package de.moyapro.nushppinglist.sync.handler

import android.util.Log
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.getCartItemByItemId
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.CartItemUpdateMessage

class CartItemUpdateMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher,
) : suspend (CartItemUpdateMessage) -> Unit {

    private val tag = CartItemUpdateMessageHandler::class.simpleName

    override suspend fun invoke(cartItemUpdateMessage: CartItemUpdateMessage) {
        Log.i(tag, "handle message $cartItemUpdateMessage")
        val existingCartItemProperties =
            cartDao.getCartItemByItemId(cartItemUpdateMessage.cartItemProperties.itemId)
        if (null == existingCartItemProperties) {
            Log.i(tag, "vvv\tcreate new item: $cartItemUpdateMessage")
            cartDao.save(cartItemUpdateMessage.cartItemProperties)
        } else {
            Log.i(tag, "vvv\tupdate item: $cartItemUpdateMessage")
            cartDao.updateAll(cartItemUpdateMessage.cartItemProperties)
        }
        Log.i(tag, "vvv\tdone handling item: $cartItemUpdateMessage")
    }
}
