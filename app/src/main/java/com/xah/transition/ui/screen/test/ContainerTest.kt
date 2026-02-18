package com.xah.transition.ui.screen.test

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.xah.common.util.ScreenCornerHelper
import com.xah.container.LocalSharedContainerRegistry
import com.xah.container.SharedContainer
import com.xah.container.SharedContainerRoot
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private enum class ScreenState {
    A, B
}

@Preview
@Composable
fun ContainerTest() {
    var currentState by remember { mutableStateOf(ScreenState.A) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    val key = remember { "test" }
    SharedContainerRoot {
        val registry = LocalSharedContainerRegistry.current

        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            when (currentState) {
                ScreenState.A -> {
                    SharedContainer(
                        key,
                        modifier = Modifier
                            .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        offsetX += dragAmount.x
                                        offsetY += dragAmount.y
                                    }
                                )
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(30.dp))
                                .background(Color.Red)
                                .clickable {
                                    scope.launch {
                                        registry.push(key) {
                                            currentState = ScreenState.B
                                        }
                                    }
                                }
                        )
                    }
                }
                ScreenState.B -> {
                    SharedContainer(
                        key,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(ScreenCornerHelper.corner))
//                                .size(200.dp)
//                                .clip(RoundedCornerShape(5.dp))
                                .background(Color.Green)
                                .clickable {
                                    scope.launch {
                                        registry.pop(key) {
                                            currentState = ScreenState.A
                                        }
                                    }
                                }
                        )
                    }
                }
            }
        }
    }
}

