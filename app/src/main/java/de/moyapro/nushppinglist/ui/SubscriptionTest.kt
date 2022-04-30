import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import de.moyapro.nushppinglist.ui.model.SubscriptionViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun SubscriptionTest(viewModel: SubscriptionViewModel) {
    val allTimes: List<LocalDateTime> by viewModel.currentTime.collectAsState(listOf())
    Column() {
        Row() {

        Button(onClick = viewModel::startTimer) {
            Text("Start Timer")
        }
        Button(onClick = viewModel::stopTimer) {
            Text("Stop Timer")
        }
        }
        allTimes.forEach { time ->
            Text(time.format(DateTimeFormatter.ISO_DATE_TIME))
        }
    }

}
