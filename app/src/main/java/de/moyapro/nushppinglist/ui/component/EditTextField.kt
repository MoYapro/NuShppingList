package de.moyapro.nushppinglist.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics

object EditTextField {
    const val DESCRIPTION = "EditTextField"
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun EditTextField(
    modifier: Modifier = Modifier,
    label: String? = null,
    initialValue: String,
    onValueChange: (String) -> Unit = {},
    onFocusChanged: (FocusState) -> Unit = {},
    widthPercentage: Float = 1.0F,
    keyboardOptions: KeyboardOptions = KeyboardOptions(),
    doneAction: () -> Unit = {},
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        label = { Label(labelText = label ?: "") },
        value = initialValue,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
                doneAction()
            },
        ),
        modifier = modifier
            .semantics { contentDescription = EditTextField.DESCRIPTION }
            .fillMaxWidth(widthPercentage)
            .onFocusChanged(onFocusChanged)
    )
}
