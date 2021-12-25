package de.moyapro.nushppinglist.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.moyapro.nushppinglist.db.model.Item

@Composable
fun KategoryIndicator(item: Item, height: Dp = 24.dp) {
    Box(modifier = Modifier
        .width(2.dp)
        .height(height)
        .background(item.kategory.color)
    )
}
