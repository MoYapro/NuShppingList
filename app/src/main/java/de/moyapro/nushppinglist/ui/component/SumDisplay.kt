package de.moyapro.nushppinglist.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.math.BigDecimal

@Composable
fun SumDisplay(total: BigDecimal) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(" Gesamtpreis")
            Text(
                modifier = Modifier.absolutePadding(right = 21.dp),
                text = "$total €")
        }
    }
}
