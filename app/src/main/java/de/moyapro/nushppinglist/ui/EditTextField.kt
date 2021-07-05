package de.moyapro.nushppinglist.ui

import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

object EditTextField {
    const val DESCRIPTION = "EditTextField"
}

@Composable
fun EditTextField(label: String, initialValue: String, onChange: (String) -> Unit) {
    Label(labelText = label)
    TextField(value = initialValue, onChange, modifier = Modifier.semantics {
        contentDescription = EditTextField.DESCRIPTION
    })
}