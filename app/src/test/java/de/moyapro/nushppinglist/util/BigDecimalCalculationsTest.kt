package de.moyapro.nushppinglist.util

import io.kotest.matchers.shouldBe
import org.junit.Test
import java.math.BigDecimal

class BigDecimalCalculationsTest {


    @Test
    fun bigDecimalListSum() {
        val data = listOf(
            BigDecimal("7.14"),
            BigDecimal("32.13"),
            BigDecimal("-7.14"),
            BigDecimal("0.00"),
        )
        data.sumByBigDecimal() shouldBe BigDecimal("32.13")
    }

    @Test
    fun bigDecimalListSum__emptyList() {
        emptyList<BigDecimal>().sumByBigDecimal() shouldBe BigDecimal("0.00")
    }
}
