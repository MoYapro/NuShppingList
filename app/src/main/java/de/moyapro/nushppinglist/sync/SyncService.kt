package de.moyapro.nushppinglist.sync

import android.util.Log
import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import de.moyapro.nushppinglist.sync.messages.ShoppingMessage
import de.moyapro.nushppinglist.ui.util.waitFor

class SyncService(
    private val serviceAdapter: MqttServiceAdapter,
    cartDao: CartDao,
) {

    private val tag = SyncService::class.simpleName

    init {
        Log.i(tag, "try to establish mqtt connection using client: ${serviceAdapter.clientIdentifier}")
        waitFor { serviceAdapter.isConnected() }
        serviceAdapter.setHandler(MessageHandler.build(serviceAdapter, cartDao))
        serviceAdapter.subscribe("${CONSTANTS.MQTT_TOPIC_BASE}/#")
        Log.i(tag, "successfully connected to mqtt server using client: ${serviceAdapter.clientIdentifier}")
    }

    fun isConnected() = serviceAdapter.isConnected()

    fun requestItem(itemId: ItemId) {
        serviceAdapter.publish(RequestItemMessage(itemId))
    }

    fun reconnect() {
        if(isConnected()) return
        serviceAdapter.connect()
    }

    fun requestCart() {
        serviceAdapter.publish(RequestCartMessage())
    }

    fun publish(messageObject: ShoppingMessage) {
        serviceAdapter.publish(messageObject)
    }

    fun shutdown() {
        serviceAdapter.disconnect()
    }
}
