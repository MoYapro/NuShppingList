package de.moyapro.nushppinglist.util

import java.math.BigDecimal

fun Collection<BigDecimal>.sumByBigDecimal(): BigDecimal {
    return if (this.isEmpty())
        BigDecimal("0.00")
    else
        this.reduce { a, b -> a.plus(b) }
}
