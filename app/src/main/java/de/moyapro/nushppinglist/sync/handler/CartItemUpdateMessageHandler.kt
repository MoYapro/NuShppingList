package de.moyapro.nushppinglist.sync.handler

import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.dao.getCartItemByItemId
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.CartItemUpdateMessage

class CartItemUpdateMessageHandler(
    val cartDao: CartDao,
    val publisher: Publisher,
) : suspend (CartItemUpdateMessage) -> Unit {

    override suspend fun invoke(cartItemUpdateMessage: CartItemUpdateMessage) {
        val existingCartItemProperties =
            cartDao.getCartItemByItemId(cartItemUpdateMessage.cartItemProperties.itemId)
        if (null == existingCartItemProperties) {
            cartDao.save(cartItemUpdateMessage.cartItemProperties)
        } else {
            cartDao.updateAll(cartItemUpdateMessage.cartItemProperties)
        }
    }
}
