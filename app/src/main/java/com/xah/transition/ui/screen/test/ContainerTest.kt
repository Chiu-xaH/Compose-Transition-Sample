package com.xah.transition.ui.screen.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.xah.container.container.SharedContainer
import com.xah.container.container.SharedContent
import com.xah.container.overlay.SharedContainerRoot
import com.xah.container.util.LocalSharedContainerRegistry
import com.xah.transition.R
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
                        corner = 20.dp,
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
                                .size(125.dp)
                                .background(Color.Red)
                                .clickable {
                                    registry.push(key) {
                                        currentState = ScreenState.B
                                    }
                                }
                        ) {
                            Image(painterResource(R.drawable.ic_jd),null)
                        }
                    }

                    SharedContainer(
                        key2,
                        corner = 15.dp,
                        modifier = Modifier
                            .padding(horizontal = APP_HORIZONTAL_DP)
                            .align(Alignment.Center)
                    ) {
                        Surface (
                            modifier = Modifier
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(
                                            MaterialTheme.colorScheme.primaryContainer,
                                            MaterialTheme.colorScheme.primary
                                        )
                                    )
                                )
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(0),
                            color = Color.Transparent
                        ) {
                            TransplantListItem(
                                headlineContent = {
                                    Text("标题")
                                },
                                supportingContent = {
                                    Text("内容")
                                },
                                modifier = Modifier.clickable {
                                    registry.push(key2) {
                                        currentState = ScreenState.C
                                    }
                                }
                            )
                        }
                    }
                }
                ScreenState.B -> {
                    SharedContent(
                        key,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
//                                 .clickable {
//                                     scope.launch {
//                                         registry.pop(key) {
//                                             currentState = ScreenState.A
//                                         }
//                                     }
//                                 }
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                        ) {
//                            return@Box
                            LazyColumn {
                                items(100) {
                                    CardListItem(
                                        headlineContent = {
                                            Text("测试")
                                        },
                                        color = MaterialTheme.colorScheme.surface,
                                        leadingContent = {
                                            Text("${it+1}")
                                        },
                                        modifier = Modifier.clickable {
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
                ScreenState.C -> {
                    SharedContent(
                        key2,
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            LazyColumn {
                                items(100) {
                                    CardListItem(
                                        headlineContent = {
                                            Text("测试")
                                        },
                                        color = MaterialTheme.colorScheme.surface,
                                        leadingContent = {
                                            Text("${it+1}")
                                        },
                                        modifier = Modifier.clickable {
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
        }
    }
}
