package de.moyapro.nushppinglist.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import de.moyapro.nushppinglist.service.BackgroundSyncService
import de.moyapro.nushppinglist.sync.MqttSingleton
import de.moyapro.nushppinglist.sync.messages.RequestCartMessage

@Composable
fun MqttTestView(context: Context) {
    Column() {
        Button(onClick = ::requestCart) {
            Text("request cart")
        }
        Button(onClick = { startBackgroundService(context) }) {
            Text("start background service")
        }
        Button(onClick = { stopBackgroundService(context) }) {
            Text("stop background service")
        }
        Text(color = MaterialTheme.colors.onBackground,
            text = if (BackgroundSyncService.isRunning) {
            "BackgroundService is running"
        } else {
            "Backgroundservice is stopped"
        })
        Text(color = MaterialTheme.colors.onBackground,
            text = if (BackgroundSyncService.isConnected()) {
            "BackgroundService is connected"
        } else {
            "Backgroundservice is disconnected"
        })
    }
}


fun requestCart() {
    MqttSingleton.adapter.publish(RequestCartMessage(null))
}

fun startBackgroundService(context: Context) {
    context.startService(Intent(context, BackgroundSyncService::class.java))
}

fun stopBackgroundService(context: Context) {
    context.stopService(Intent(context, BackgroundSyncService::class.java))
}
