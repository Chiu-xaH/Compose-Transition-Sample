package com.xah.transition.ui.screen.test

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.xah.container.ui.container.SharedContainer
import com.xah.container.ui.overlay.SharedContainerRoot
import com.xah.container.ui.util.LocalSharedContainerRegistry
import com.xah.transition.ui.component.APP_HORIZONTAL_DP
import com.xah.transition.ui.component.CardListItem
import com.xah.transition.ui.component.TransplantListItem
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private enum class ScreenState {
    A, B,C
}

@Preview
@Composable
fun ContainerTest() {
    var currentState by remember { mutableStateOf(ScreenState.A) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()
    val key = remember { "test" }
    val key2 = remember { "test2" }
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
                    SharedContainer(
                        key2,
                        modifier = Modifier
                            .padding(horizontal = APP_HORIZONTAL_DP)
                            .align(Alignment.Center)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer,
                            shape = MaterialTheme.shapes.medium
                        ) {
                            TransplantListItem(
                                headlineContent = {
                                    Text("测试")
                                },
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        registry.push(key2) {
                                            currentState = ScreenState.C
                                        }
                                    }
                                }
                            )
                        }

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
//                                .clip(RoundedCornerShape(ScreenCornerHelper.corner))
//                                .size(200.dp)
//                                .size(width = 200.dp, height = 300.dp)
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
                ScreenState.C -> {
                    SharedContainer(
                        key2,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
//                                .clip(RoundedCornerShape(ScreenCornerHelper.corner))
//                                .size(200.dp)
//                                .size(width = 200.dp, height = 300.dp)
//                                .clip(RoundedCornerShape(5.dp))
                                .background(Color.Black)
                                .clickable {
                                    scope.launch {
                                        registry.pop(key2) {
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


@Preview
@Composable
fun ContainerRecordDemo() {
    var currentState by remember { mutableStateOf(ScreenState.A) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    Box(Modifier.fillMaxSize()) {
        // -------------------------
        // 1️⃣ Screen A
        if (currentState == ScreenState.A) {
            Box(
                modifier = Modifier
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color.Red)
                        .clickable {
                            scope.launch {
                                currentState = ScreenState.B
                            }
                        }
                )
            }
        }

        // -------------------------
        // 2️⃣ Screen B
        if (currentState == ScreenState.B) {
            // B 的内容
            Box(
                modifier = Modifier
                    .size(width = 200.dp, height = 300.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.Green)
                    .align(Alignment.Center)
                    .clickable {
                        // 回到 A
                        scope.launch {
                            currentState = ScreenState.A
                        }
                    }
            )
        }
    }
}