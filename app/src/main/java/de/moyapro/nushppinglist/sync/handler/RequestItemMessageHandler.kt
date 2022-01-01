package de.moyapro.nushppinglist.sync.handler

import de.moyapro.nushppinglist.constants.CONSTANTS
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.RequestItemMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel

class RequestItemMessageHandler(
    val viewModel: CartViewModel,
    val publisher: Publisher,
) : (RequestItemMessage) -> Unit {

    override fun invoke(requestItemMessage: RequestItemMessage) {
        val item = viewModel.getItemByItemId(requestItemMessage.itemId)
        if (null != item) {
            publisher.publish(CONSTANTS.MQTT_TOPIC_ITEM, item)
        }
    }
}
