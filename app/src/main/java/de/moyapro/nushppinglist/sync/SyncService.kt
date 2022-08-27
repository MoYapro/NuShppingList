package de.moyapro.nushppinglist.sync

import android.util.Log
import de.moyapro.nushppinglist.db.dao.CartDao
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import de.moyapro.nushppinglist.sync.messages.ShoppingMessage
import de.moyapro.nushppinglist.ui.util.waitFor

class SyncService(
    private val serviceAdapter: MqttServiceAdapter,
    cartDao: CartDao,
) {

    private val tag = SyncService::class.simpleName

    init {
        Log.d(tag, "try to establish mqtt connection using client: ${serviceAdapter.clientIdentifier}")
        if(waitFor { serviceAdapter.isConnected() }) {
            serviceAdapter.setHandler(MessageHandler.build(serviceAdapter, cartDao))
            serviceAdapter.subscribe()
            Log.d(tag,
                "successfully connected to mqtt server using client: ${serviceAdapter.clientIdentifier}")
        } else {
            Log.i(tag, "Could not connect to sync service")
        }
    }

    fun isConnected() = serviceAdapter.isConnected()

    fun requestItem(itemId: ItemId) {
        serviceAdapter.publish(RequestItemMessage(itemId))
    }

    fun publish(messageObject: ShoppingMessage) {
        serviceAdapter.publish(messageObject)
    }

    fun shutdown() {
        serviceAdapter.disconnect()
    }
}
