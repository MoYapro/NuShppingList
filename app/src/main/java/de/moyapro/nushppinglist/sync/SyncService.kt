package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.sync.handler.*
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import de.moyapro.nushppinglist.sync.messages.ShoppingMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.waitFor

class SyncService(
    private val serviceAdapter: MqttServiceAdapter,
    viewModel: CartViewModel,
    cartDao: CartDao,
) {

    init {
        waitFor { serviceAdapter.isConnected() }
        serviceAdapter.setHandler(buildHandler(viewModel, serviceAdapter, cartDao))
        serviceAdapter.subscribe("${CONSTANTS.MQTT_TOPIC_BASE}/#")
    }

    private fun buildHandler(viewModel: CartViewModel, publisher: Publisher, cartDao: CartDao) =
        MessageHandler(
            requestItemMessageHandler = RequestItemMessageHandler(viewModel, publisher),
            requestCartMessageHandler = RequestCartMessageHandler(viewModel, publisher),
            itemMessageHandler = ItemMessageHandler(cartDao, publisher),
            cartMessageHandler = CartMessageHandler(viewModel, publisher),
            cartItemUpdateMessageHandler = CartItemUpdateMessageHandler(viewModel, publisher),
        )


    fun requestItem(itemId: ItemId) {
        serviceAdapter.publish(RequestItemMessage(itemId))
    }

    fun requestCart() {
        serviceAdapter.publish(RequestCartMessage())
    }

    fun publish(messageObject: ShoppingMessage) {
        serviceAdapter.publish(messageObject)
    }
}
