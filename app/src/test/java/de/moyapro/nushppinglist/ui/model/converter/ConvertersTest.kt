package de.moyapro.nushppinglist.ui.model.converter

import io.kotest.matchers.shouldBe
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

class ConvertersTest {

    private lateinit var sut: Converters

    @Before
    fun setUp() {
        sut = Converters()
    }

    @Test
    fun fromLong() {
        sut.fromLong(1299) shouldBe BigDecimal("12.99")
        sut.fromLong(901) shouldBe BigDecimal("9.01")
        sut.fromLong(100) shouldBe BigDecimal("1.00")
        sut.fromLong(120) shouldBe BigDecimal("1.20")
        sut.fromLong(1) shouldBe BigDecimal("0.01")
    }

    @Test
    fun toLong() {
        sut.toLong(BigDecimal("12.99")) shouldBe 1299
        sut.toLong(BigDecimal("9.01")) shouldBe 901
        sut.toLong(BigDecimal("1.00")) shouldBe 100
        sut.toLong(BigDecimal("0.01")) shouldBe 1
    }
}
