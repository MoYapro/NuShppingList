package de.moyapro.nushppinglist.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import de.moyapro.nushppinglist.db.AppDatabase
import de.moyapro.nushppinglist.sync.MqttSingleton
import de.moyapro.nushppinglist.sync.SyncService


class BackgroundSyncService : Service() {

    companion object {
        var isRunning = false
        fun isConnected() = syncService?.isConnected() ?: false
        private var syncService: SyncService? = null
    }

    val database by lazy { AppDatabase.getDatabase(this) }
    private val tag = BackgroundSyncService::class.simpleName

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(tag, "onCreate")
        Toast.makeText(this, "Invoke background service onCreate method.", Toast.LENGTH_LONG).show()
        if (!isConnected()) {
            syncService = SyncService(MqttSingleton.adapter, database.cartDao())
        }
        isRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(tag, "onStartCommand: connected: ${syncService?.isConnected()}")
        val superReturnStatus = super.onStartCommand(intent, flags, startId)
        Toast.makeText(this, "Invoke background service onStartCommand method.", Toast.LENGTH_LONG)
            .show()
        syncService?.reconnect()
        return superReturnStatus
    }

    override fun onDestroy() {
        Log.i(tag, "onDestroy")
        super.onDestroy()
        syncService?.shutdown()
        syncService = null
        isRunning = false
        Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG)
            .show()
    }

}
