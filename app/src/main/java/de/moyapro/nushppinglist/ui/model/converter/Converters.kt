package de.moyapro.nushppinglist.ui.model.converter

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP

class Converters {
    @TypeConverter
    fun fromLong(value: Long?): BigDecimal? {
        return if (value == null) null else BigDecimal(value).divide(BigDecimal(100))
            .setScale(2, HALF_UP)
    }

    @TypeConverter
    fun toLong(bigDecimal: BigDecimal?): Long? {
        return bigDecimal?.multiply(BigDecimal(100))?.toLong()
    }
}
