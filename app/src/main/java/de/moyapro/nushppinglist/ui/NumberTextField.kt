package de.moyapro.nushppinglist.ui

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP

object NumberTextField {
    const val DESCRIPTION = "NumberTextField"
    fun bigDecimalFromStringInput(text: String): BigDecimal {
        val fixedText = text
            .replace(',', '.')
            .replace(Regex("[^0-9.]"), "")
        if (isEmptyValue(fixedText)) return BigDecimal.ZERO.setScale(2)
        return BigDecimal(fixedText).setScale(2, HALF_UP)
    }

    private fun isEmptyValue(text: String): Boolean {
        return when {
            text.isBlank() -> true
            text == "." -> true
            text == "," -> true
            text.matches("^[a-zA-Z]*$".toRegex()) -> true
            else -> false
        }
    }
}

@Composable
fun NumberTextField(
    label: String? = null,
    initialValue: BigDecimal,
    onValueChange: (BigDecimal) -> Unit,
) {
    OutlinedTextField(
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        value = initialValue.toString(),
        label = { Label(labelText = label ?: "") },
        onValueChange = { newTextValue ->
            onValueChange(NumberTextField.bigDecimalFromStringInput(newTextValue))
        },
        modifier = Modifier
            .semantics {
                contentDescription = NumberTextField.DESCRIPTION
            }
    )
}
