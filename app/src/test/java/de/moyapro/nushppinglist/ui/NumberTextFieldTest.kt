package de.moyapro.nushppinglist.ui

import io.kotest.matchers.shouldBe
import org.junit.Test

class NumberTextFieldTest {

    private val sut = NumberTextField

    @Test
    fun bigDecimalFromStringInput__simple() {
        sut.bigDecimalFromStringInput("1").toString() shouldBe "1.00"
        sut.bigDecimalFromStringInput("1.0").toString() shouldBe "1.00"
        sut.bigDecimalFromStringInput("0.01").toString() shouldBe "0.01"
        sut.bigDecimalFromStringInput("1.23").toString() shouldBe "1.23"
    }

    @Test
    fun bigDecimalFromStringInput__withCommaAndDot() {
        sut.bigDecimalFromStringInput("1,23").toString() shouldBe "1.23"
        sut.bigDecimalFromStringInput("1,20").toString() shouldBe "1.20"
    }

    @Test
    fun bigDecimalFromStringInput__empty() {
        sut.bigDecimalFromStringInput("").toString() shouldBe "0.00"
    }

    @Test
    fun bigDecimalFromStringInput__dot() {
        sut.bigDecimalFromStringInput(".").toString() shouldBe "0.00"
    }

    @Test
    fun bigDecimalFromStringInput__comma() {
        sut.bigDecimalFromStringInput(",").toString() shouldBe "0.00"
    }

    @Test
    fun bigDecimalFromStringInput__nonDigits() {
        sut.bigDecimalFromStringInput("abCCC#$^*$^&").toString() shouldBe "0.00"
    }

    @Test
    fun bigDecimalFromStringInput__nonDigitsMixed() {
        sut.bigDecimalFromStringInput("abC2,99CC#$^*$^&").toString() shouldBe "2.99"
    }

    @Test
    fun bigDecimalFromStringInput__endsWithDot() {
        sut.bigDecimalFromStringInput("12.").toString() shouldBe "12.00"
    }

    @Test
    fun bigDecimalFromStringInput__endsWithComma() {
        sut.bigDecimalFromStringInput("12,").toString() shouldBe "12.00"
    }



}
