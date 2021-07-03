package de.moyapro.nushppinglist.ui

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter


object Label {
    const val DESCRIPTION = "Lable"
}

@Preview
@Composable
fun Label(@PreviewParameter(LabelProvider::class) label: String) {
    Text(text = "Beatle", modifier = Modifier.semantics {
        contentDescription = Label.DESCRIPTION
    })
}