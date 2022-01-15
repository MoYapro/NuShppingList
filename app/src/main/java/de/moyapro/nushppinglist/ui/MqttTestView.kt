package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import de.moyapro.nushppinglist.sync.MqttSingleton
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage

@Composable
fun MqttTestView() {
 Column() {
     Button(onClick = ::requestCart) {
         Text("request cart")
     }
 }
}


fun requestCart() {
    MqttSingleton.adapter.publish(RequestCartMessage())
}
