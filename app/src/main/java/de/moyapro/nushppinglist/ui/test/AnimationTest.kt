package de.moyapro.nushppinglist.ui.test

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun AnimationTest() {
    Column() {

        var longPressed by remember { mutableStateOf(false) }
        val longPressColor: Color = colorChange(longPressed)
        val longPressSize: Dp = sizeChange(longPressed)

        var touched by remember { mutableStateOf(false) }
        val touchColor: Color by animateColorAsState(targetValue = colorChange(touched), animationSpec = tween(1000))
        val touchSize: Dp by animateDpAsState(targetValue = sizeChange(touched),
            animationSpec = tween(1000),
            finishedListener = {
                if(it > 20.dp) longPressed = !longPressed
            })

        Row {

            Box(
                modifier = Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = {
                                try {
                                    touched = true
                                    awaitRelease()
                                } finally {
                                    touched = false
                                }
                            },
                        )
                    }
                    .height(35.dp)
                    .width(150.dp)
                    .background(MaterialTheme.colors.surface),
            ) {
                Text(text = "Box",
                    color = contentColorFor(backgroundColor = MaterialTheme.colors.surface))
            }


            val interactionSource = remember { MutableInteractionSource() }
            val isPressed by interactionSource.collectIsPressedAsState()

            if (isPressed) {
                touched = true
                //Use if + DisposableEffect to wait for the press action is completed
                DisposableEffect(Unit) {
                    onDispose {
                        touched = false
                    }
                }
            }

            Button(
                modifier = Modifier
                    .weight(2f)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                longPressed = !longPressed
                            }
                        )
                    },
                onClick = {},
                interactionSource = interactionSource
            ) {
                Text("Button")
            }


        }

        Surface() {
            Row {
                Column {
                    Text(text = "touched")
                    Box(modifier = Modifier
                        .width(touchSize)
                        .height(touchSize)
                        .background(touchColor))
                }
                Column {
                    Text(text = "long pressed")
                    Box(modifier = Modifier
                        .width(longPressSize)
                        .height(longPressSize)
                        .background(longPressColor))
                }
            }
        }
    }
}


private fun sizeChange(touched: Boolean) = if (touched) 30.dp else 20.dp
fun colorChange(value: Boolean): Color = if (value) Color.Green else Color.Red
