package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.sync.handler.CartMessageHandler
import de.moyapro.nushppinglist.sync.handler.ItemMessageHandler
import de.moyapro.nushppinglist.sync.handler.RequestCartMessageHandler
import de.moyapro.nushppinglist.sync.handler.RequestItemMessageHandler
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import de.moyapro.nushppinglist.sync.messages.ShoppingMessage
import de.moyapro.nushppinglist.ui.util.waitFor

class SyncService(
    private val serviceAdapter: MqttServiceAdapter,
    cartDao: CartDao,
) {

    init {
        waitFor { serviceAdapter.isConnected() }
        serviceAdapter.setHandler(buildHandler(serviceAdapter, cartDao))
        serviceAdapter.subscribe("${CONSTANTS.MQTT_TOPIC_BASE}/#")
    }

    private fun buildHandler(publisher: Publisher, cartDao: CartDao) =
        MessageHandler(
            requestItemMessageHandler = RequestItemMessageHandler(cartDao, publisher),
            requestCartMessageHandler = RequestCartMessageHandler(cartDao, publisher),
            itemMessageHandler = ItemMessageHandler(cartDao, publisher),
            cartMessageHandler = CartMessageHandler(cartDao, publisher),
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
