package de.moyapro.nushppinglist.component

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import de.moyapro.nushppinglist.ui.component.DecimalTextField
import de.moyapro.nushppinglist.ui.theme.NuShppingListTheme
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

internal class DecimalTextFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun numberAndLabelAreShown() {
        val initialValue = BigDecimal(4711.33)
        val label = "label"
        composeTestRule.setContent {
            NuShppingListTheme {
                DecimalTextField(
                    label,
                    initialValue
                )
            }
        }
        composeTestRule.onAllNodesWithContentDescription(DecimalTextField.DESCRIPTION)
            .assertCountEquals(1)
        composeTestRule.onAllNodesWithContentDescription(DecimalTextField.DESCRIPTION)
            .assertAll(hasText(initialValue.toString()))
        composeTestRule.onAllNodesWithContentDescription(DecimalTextField.DESCRIPTION)
            .assertAll(hasText(label))
    }

    @Test
    fun editValue() {
        val initialValue = BigDecimal("1.33")
        var value = initialValue
        val label = "label"
        composeTestRule.setContent {
            NuShppingListTheme {
                DecimalTextField(
                    label,
                    initialValue,
                    onValueChange = { value = it }
                )
            }
        }
        val component =
            composeTestRule.onAllNodesWithContentDescription(DecimalTextField.DESCRIPTION)
        Thread.sleep(2000)
        component.onFirst().performTextInput("44")
        Thread.sleep(2000)

        component.assertCountEquals(1)
        component.assertAll(hasText("133.44"))
        component.assertAll(hasText(label))
        value shouldBe BigDecimal("133.44")
        Thread.sleep(5000)
    }

}
