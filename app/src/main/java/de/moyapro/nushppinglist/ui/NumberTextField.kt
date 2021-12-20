package de.moyapro.nushppinglist.ui

import android.util.Log
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP
import java.util.*

private const val TAG = "NumberTextField"

object NumberTextField {
    const val DESCRIPTION = "NumberTextField"
    fun bigDecimalFromStringInput(text: String): BigDecimal {
        val conversionId = UUID.randomUUID()
        Log.i(TAG, "$conversionId - convert text to bigDecimal: String was $text")
        val onlyNumberChars = text.replace(Regex("[^0-9]"), "").padStart(3, '0')

        val numbersWithAddedDot = onlyNumberChars.replaceRange(
            startIndex = onlyNumberChars.length - 2,
            endIndex = onlyNumberChars.length - 2,
            replacement = ".")

        Log.i(TAG, "$conversionId - convert text to bigDecimal: String now $text")
        return BigDecimal(numbersWithAddedDot).setScale(2, HALF_UP)
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
