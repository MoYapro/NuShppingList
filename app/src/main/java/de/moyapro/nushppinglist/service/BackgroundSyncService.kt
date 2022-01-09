package de.moyapro.nushppinglist.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import de.moyapro.nushppinglist.db.AppDatabase
import de.moyapro.nushppinglist.db.dao.getCartItemByItemId
import de.moyapro.nushppinglist.db.ids.ItemId
import de.moyapro.nushppinglist.mock.CartDaoMock
import de.moyapro.nushppinglist.sync.MqttServiceAdapter
import de.moyapro.nushppinglist.sync.SyncService
import de.moyapro.nushppinglist.ui.model.CartViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking


class BackgroundSyncService : Service() {

    val database by lazy { AppDatabase.getDatabase(this) }

    val tag = BackgroundSyncService::class.simpleName

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        Log.i(tag,"onCreate")
        Toast.makeText(this, "Invoke background service onCreate method.", Toast.LENGTH_LONG).show()
        val x = runBlocking {
            database.cartDao().getCartItemByItemId(ItemId())
        }
        println("$x")
        SyncService(MqttServiceAdapter("").connect(),
            CartViewModel(CartDaoMock(CoroutineScope(Dispatchers.IO + SupervisorJob()))))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(tag,"onStartCommand")
        return super.onStartCommand(intent, flags, startId)
        Toast.makeText(this, "Invoke background service onStartCommand method.", Toast.LENGTH_LONG)
            .show()
    }

    override fun onDestroy() {
        Log.i(tag,"onDestroy")
        super.onDestroy()
        Toast.makeText(this, "Invoke background service onDestroy method.", Toast.LENGTH_LONG)
            .show()
    }

}
