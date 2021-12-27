package de.moyapro.nushppinglist.ui.component

import android.widget.Toast
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HoldableButton(
    width: Dp = 150.dp,
    height: Dp = 35.dp,
    animationDuration: Int = 1000,
    onLongPress: () -> Unit,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    holdHintText: String = "",
    content: @Composable RowScope.() -> Unit,
) {


    // internal vars
    var holdHasStarted by remember { mutableStateOf(false) }
    val alpha: Float by animateFloatAsState(targetValue = if (holdHasStarted) 1F else 0F)
    val touchColor: Color by animateColorAsState(targetValue = if (holdHasStarted) MaterialTheme.colors.error else MaterialTheme.colors.primary,
        animationSpec = tween(animationDuration))
    val touchSize: Dp by animateDpAsState(targetValue = if (holdHasStarted) width else 0.dp,
        animationSpec = tween(animationDuration),
        finishedListener = { if (it >= width) onLongPress() })
    val context = LocalContext.current

    Row {

        val interactionSource = remember { MutableInteractionSource() }
        val isPressed by interactionSource.collectIsPressedAsState()

        if (isPressed) {
            holdHasStarted = true
            DisposableEffect(Unit) {
                onDispose {
                    holdHasStarted = false
                }
            }
        }
        Row() {
            Button(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = { onLongPress() }
                        )
                    },
                onClick = {
                    if (holdHintText.isNotBlank())
                        Toast.makeText(
                            context,
                            holdHintText,
                            Toast.LENGTH_SHORT
                        ).show()
                },
                interactionSource = interactionSource,
                colors = colors,
                content = content
            )
        }
        Row(modifier = Modifier.alpha(alpha)) {
            Box {
                Box(modifier = Modifier
                    .width(width)
                    .height(height)
                    .background(MaterialTheme.colors.primary))
                Box(modifier = Modifier
                    .width(touchSize)
                    .height(height)
                    .background(touchColor))
            }
        }
    }
}
