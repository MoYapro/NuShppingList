package de.moyapro.nushppinglist.sync.handler

import androidx.lifecycle.viewModelScope
import de.moyapro.nushppinglist.sync.Publisher
import de.moyapro.nushppinglist.sync.messages.CartMessage
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage
import de.moyapro.nushppinglist.ui.model.CartViewModel
import kotlinx.coroutines.launch

class RequestCartMessageHandler(
    val viewModel: CartViewModel,
    val publisher: Publisher,
) : (RequestCartMessage) -> Unit {

    override fun invoke(requestCartMessage: RequestCartMessage) {
        viewModel.viewModelScope.launch {
            publisher.publish(
                CartMessage(viewModel.getAllCartItemProperties().map { it.cartItemProperties })
            )
        }
    }
}
