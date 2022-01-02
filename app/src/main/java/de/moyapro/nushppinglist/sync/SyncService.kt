package de.moyapro.nushppinglist.sync

import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.sync.handler.*
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel
import de.moyapro.nushppinglist.ui.util.waitFor

class SyncService(
    private val serviceAdapter: MqttServiceAdapter,
    viewModel: CartViewModel,
) {

    init {
        waitFor { serviceAdapter.isConnected() }
        serviceAdapter.setHandler(buildHandler(viewModel, serviceAdapter))
        serviceAdapter.subscribe("${CONSTANTS.MQTT_TOPIC_BASE}/#")
    }

    private fun buildHandler(viewModel: CartViewModel, publisher: Publisher) = MessageHandler(
        requestItemMessageHandler = RequestItemMessageHandler(viewModel, publisher),
        requestCartMessageHandler = RequestCartMessageHandler(viewModel, publisher),
        itemMessageHandler = ItemMessageHandler(viewModel, publisher),
        cartMessageHandler = CartMessageHandler(viewModel, publisher),
        cartItemUpdateMessageHandler = CartItemUpdateMessageHandler(viewModel, publisher),
    )


    fun requestItem(itemId: ItemId) {
        val requestItemMessage = RequestItemMessage(itemId)
        serviceAdapter.publish(requestItemMessage)
    }
}
