package de.moyapro.nushppinglist.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import java.math.BigDecimal
import java.math.RoundingMode.HALF_UP

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DecimalTextField(
    label: String? = null,
    initialValue: BigDecimal,
    onValueChange: (BigDecimal) -> Unit,
) {

    OutlinedTextField(
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        value = TextFieldValue(text = initialValue.toString(),
            selection = TextRange(initialValue.toString().length)),
        label = { Label(labelText = label ?: "") },
        onValueChange = { newTextValue ->
            onValueChange(DecimalTextField.bigDecimalFromStringInput(newTextValue.text))
        },
        modifier = Modifier
            .semantics { contentDescription = DecimalTextField.DESCRIPTION }
            .fillMaxWidth()
    )
}

object DecimalTextField {
    const val DESCRIPTION = "DecimalTextField"
    fun bigDecimalFromStringInput(text: String): BigDecimal {
        val onlyNumberChars = text.replace(Regex("[^0-9]"), "").padStart(3, '0')
        val numbersWithAddedDecimal = onlyNumberChars.replaceRange(
            startIndex = onlyNumberChars.length - 2,
            endIndex = onlyNumberChars.length - 2,
            replacement = ".")
        return BigDecimal(numbersWithAddedDecimal).setScale(2, HALF_UP)
    }
}
