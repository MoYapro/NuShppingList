package de.moyapro.nushppinglist.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
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
    modifier: Modifier = Modifier,
    label: String? = null,
    initialValue: String,
    onValueChange: (String) -> Unit,
    widthPercentage: Float =  1.0F,
) {
    OutlinedTextField(
        label = { Label(labelText = label ?: "") },
        value = initialValue,
        onValueChange = onValueChange,
        modifier = modifier
            .semantics { contentDescription = EditTextField.DESCRIPTION }
            .fillMaxWidth(widthPercentage)
    )
}
