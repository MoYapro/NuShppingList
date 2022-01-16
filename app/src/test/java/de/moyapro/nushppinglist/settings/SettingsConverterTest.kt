package de.moyapro.nushppinglist.settings

import io.kotest.matchers.shouldBe
import org.junit.Test

class SettingsConverterTest {
    @Test
    fun splitHostnameAndPort() {
        val hostnameSetting = "my.domain.com:1883"
        val (hostname, port) = SettingsConverter.splitHostnamePort(hostnameSetting)
        hostname shouldBe "my.domain.com"
        port shouldBe 1883
    }
}

