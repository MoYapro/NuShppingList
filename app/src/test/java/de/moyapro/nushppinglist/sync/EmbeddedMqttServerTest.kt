package de.moyapro.nushppinglist.sync

import android.content.SharedPreferences
import de.moyapro.nushppinglist.MainActivity
import de.moyapro.nushppinglist.constants.SETTING
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.moquette.BrokerConstants.*
import io.moquette.broker.Server
import io.moquette.broker.config.IConfig
import io.moquette.broker.config.MemoryConfig
import org.junit.BeforeClass
import org.junit.Test
import java.util.*

class EmbeddedMqttServerTest {

    init {
        MainActivity.preferences = initPreferences()
        startServer()
    }

    private fun initPreferences(): SharedPreferences {
        // init preferences before init connection
        val preferences = mockk<SharedPreferences>()
        every {
            preferences.getString(
                SETTING.SYNC_MQTT_SERVER_HOSTNAME.name,
                any()
            )
        } returns "localhost:31883"
        every {
            preferences.getString(SETTING.SYNC_MQTT_SERVER_USER.name, any())
        } returns ""
        every {
            preferences.getString(SETTING.SYNC_MQTT_SERVER_PASSWORD.name, any())
        } returns ""
        every {
            preferences.getString(SETTING.SYNC_MQTT_SERVER_BASE_TOPIC.name, any())
        } returns "nuShoppingList"
        every { preferences.getBoolean(SETTING.SYNC_MQTT_SERVER_TLS.name, any()) } returns false
        every { preferences.getBoolean(SETTING.SYNC_ENABLED.name, any()) } returns true
        return preferences
    }


    companion object {
        @BeforeClass
        fun startServer() {
            val server = Server()
            val config: IConfig = defaultMqttBrokerConfig()
            server.startServer(config)
            println("Server started, version 0.16-SNAPSHOT")
            //Bind a shutdown hook
            Runtime.getRuntime().addShutdownHook(Thread {
                println("shutting down mqtt server")
                server.stopServer()
            })
        }

        private fun defaultMqttBrokerConfig(): IConfig {
            val properties = Properties()
            properties[PORT_PROPERTY_NAME] = "31883"
            properties[HOST_PROPERTY_NAME] = "localhost"
            properties[WEB_SOCKET_PORT_PROPERTY_NAME] = WEBSOCKET_PORT
            properties[PASSWORD_FILE_PROPERTY_NAME] = ""
            properties[PERSISTENT_STORE_PROPERTY_NAME] = DEFAULT_PERSISTENT_PATH
            properties[ALLOW_ANONYMOUS_PROPERTY_NAME] = true
            properties[AUTHENTICATOR_CLASS_NAME] = ""
            properties[AUTHORIZATOR_CLASS_NAME] = ""
            return MemoryConfig(properties)
        }
    }

    @Test
    fun getConnection() {
        Thread.sleep(10000)
        val client = MqttServiceAdapter().connect()
        Thread.sleep(1000)
        client.isConnected() shouldBe true
    }
}
