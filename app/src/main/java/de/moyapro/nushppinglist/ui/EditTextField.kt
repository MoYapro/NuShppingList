package de.moyapro.nushppinglist.ui

import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

object EditTextField {
    const val DESCRIPTION = "EditTextField"
}

@Composable
fun EditTextField(
    label: String? = null,
    initialValue: String,
    onValueChange: (String) -> Unit,
) {
    OutlinedTextField(
        label = { Label(labelText = label ?: "") },
        value = initialValue,
        onValueChange = onValueChange,
        modifier = Modifier
            .semantics {
                contentDescription = EditTextField.DESCRIPTION
            }
    )
}
