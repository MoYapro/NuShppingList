package de.moyapro.nushppinglist.ui.component

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

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NumberTextField(
    modifier: Modifier = Modifier,
    label: String? = null,
    initialValue: Int,
    onValueChange: (Int) -> Unit,
) {

    OutlinedTextField(
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        value = TextFieldValue(text = initialValue.toString(),
            selection = TextRange(initialValue.toString().length)),
        label = { Label(labelText = label ?: "") },
        onValueChange = { newTextValue ->
            onValueChange(NumberTextField.intFromStringInput(newTextValue.text))
        },
        modifier = modifier
            .semantics { contentDescription = NumberTextField.DESCRIPTION }
    )
}

object NumberTextField {
    const val DESCRIPTION = "NumberTextField"
    fun intFromStringInput(text: String): Int {
        val onlyNumberChars = text.replace(Regex("[^0-9]"), "")
        return if (onlyNumberChars.isBlank()) 0 else onlyNumberChars.toInt()
    }
}
