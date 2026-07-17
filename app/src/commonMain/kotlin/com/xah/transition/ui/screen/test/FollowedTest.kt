package com.xah.transition.ui.screen.test

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.xah.transition.ui.component.APP_HORIZONTAL_DP
import com.xah.transition.ui.component.CardListItem
import com.xah.transition.ui.component.TopBarNavigationIcon
import com.xah.transition.util.PlatformBackHandler
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sharednav.app.generated.resources.Res
import sharednav.app.generated.resources.ic_settings
import kotlin.math.roundToInt

@Preview
@Composable
fun FollowedTest() {
    var isDragging by remember { mutableStateOf(false) }
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(APP_HORIZONTAL_DP)
                .statusBarsPadding()
        ) {
            Icon(
                painterResource(Res.drawable.ic_settings),
                null,
                modifier = Modifier
            )
        }
    }
}

@Composable
@Preview
fun DraggableFollowIcon2() {
    val scope = rememberCoroutineScope()

    var dragging by remember { mutableStateOf(false) }

    // 是否锁定在底部（关键状态）
    var locked by remember { mutableStateOf(false) }

    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }

    val density = LocalDensity.current
    // 最大拖拽距离（px）
    val maxOffsetY = with(density) { 50.dp.toPx() }

    val displayOrigin = !dragging && !locked

    // 触发锁定的阈值
    val lockThreshold = maxOffsetY * 0.7f

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        TopBarNavigationIcon(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(APP_HORIZONTAL_DP)
                .statusBarsPadding()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            dragging = true
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()

                            scope.launch {
                                val newY = (offset.value.y + dragAmount.y)
                                    .coerceIn(0f, maxOffsetY) // 限制范围

                                // 只允许竖直
                                offset.snapTo(Offset(0f, newY))

                                // 如果已经锁定，但用户往上拖 -> 解锁
                                if (locked && newY < lockThreshold) {
                                    locked = false
                                }
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                val currentY = offset.value.y

                                if (currentY >= lockThreshold) {
                                    // 👉 到达阈值 -> 锁定，不回弹
                                    locked = true
                                    offset.animateTo(
                                        Offset(0f, maxOffsetY),
                                        animationSpec = spring()
                                    )
                                } else {
                                    // 👉 未达到 -> 回弹
                                    locked = false
                                    offset.animateTo(
                                        Offset.Zero,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    )
                                }

                                dragging = false
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                offset.animateTo(Offset.Zero)
                                dragging = false
                                locked = false
                            }
                        }
                    )
                }
                .drawWithContent {
                    if (displayOrigin) {
                        drawContent()
                    }
                }
        )
//        Icon(
//            painter = painterResource(Res.drawable.ic_arrow_back),
//            contentDescription = null,
//            modifier = Modifier
//                .align(Alignment.TopStart)
//                .padding(APP_HORIZONTAL_DP)
//                .statusBarsPadding()
//                .pointerInput(Unit) {
//                    detectDragGestures(
//                        onDragStart = {
//                            dragging = true
//                        },
//                        onDrag = { change, dragAmount ->
//                            change.consume()
//
//                            scope.launch {
//                                val newY = (offset.value.y + dragAmount.y)
//                                    .coerceIn(0f, maxOffsetY) // 限制范围
//
//                                // 只允许竖直
//                                offset.snapTo(Offset(0f, newY))
//
//                                // 如果已经锁定，但用户往上拖 -> 解锁
//                                if (locked && newY < lockThreshold) {
//                                    locked = false
//                                }
//                            }
//                        },
//                        onDragEnd = {
//                            scope.launch {
//                                val currentY = offset.value.y
//
//                                if (currentY >= lockThreshold) {
//                                    // 👉 到达阈值 -> 锁定，不回弹
//                                    locked = true
//                                    offset.animateTo(
//                                        Offset(0f, maxOffsetY),
//                                        animationSpec = spring()
//                                    )
//                                } else {
//                                    // 👉 未达到 -> 回弹
//                                    locked = false
//                                    offset.animateTo(
//                                        Offset.Zero,
//                                        animationSpec = spring(
//                                            dampingRatio = Spring.DampingRatioMediumBouncy,
//                                            stiffness = Spring.StiffnessLow
//                                        )
//                                    )
//                                }
//
//                                dragging = false
//                            }
//                        },
//                        onDragCancel = {
//                            scope.launch {
//                                offset.animateTo(Offset.Zero)
//                                dragging = false
//                                locked = false
//                            }
//                        }
//                    )
//                }
//                .drawWithContent {
//                    if (displayOrigin) {
//                        drawContent()
//                    }
//                }
//        )

        AnimatedVisibility(
            visible = !displayOrigin ,
            enter = scaleIn(initialScale = 0.875f) + fadeIn(),
            exit = scaleOut(targetScale = 0.875f) + fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            PlatformBackHandler {
                scope.launch {
                    dragging = false
                    offset.animateTo(
                        Offset.Zero,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                    locked = false
                }
            }
            LazyColumn {
                items(50) {
                    CardListItem(
                        headlineContent = {
                            Text("Item #$it")
                        }
                    )
                }
            }
        }

        // shadow
        if (!displayOrigin || offset.value != Offset.Zero) {
            TopBarNavigationIcon(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(APP_HORIZONTAL_DP)
                    .statusBarsPadding()
                    .offset {
                        IntOffset(
                            0,
                            offset.value.y.roundToInt()
                        )
                    }
            )
//            Icon(
//                painter = painterResource(Res.drawable.ic_arrow_back),
//                contentDescription = null,
//                modifier = Modifier
//                    .align(Alignment.TopStart)
//                    .padding(APP_HORIZONTAL_DP)
//                    .statusBarsPadding()
//
//            )
        }
    }
}

@Composable
@Preview
fun DraggableFollowIcon() {
    val scope = rememberCoroutineScope()

    // 是否正在拖拽
    var dragging by remember { mutableStateOf(false) }

    // 拖拽偏移
    val offset = remember { Animatable(Offset.Zero, Offset.VectorConverter) }


    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        // 原始 Icon（拖拽时隐藏内容）
        Icon(
            painter = painterResource(Res.drawable.ic_settings),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(APP_HORIZONTAL_DP)
                .statusBarsPadding()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            dragging = true
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            scope.launch {
                                offset.snapTo(offset.value + dragAmount)
                            }
                        },
                        onDragEnd = {
                            scope.launch {
                                offset.animateTo(
                                    Offset.Zero,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessLow
                                    )
                                )
                                dragging = false
                            }
                        },
                        onDragCancel = {
                            scope.launch {
                                offset.animateTo(Offset.Zero)
                                dragging = false
                            }
                        }
                    )
                }
                .drawWithContent {
                    // 拖拽时不画原内容
                    if (!dragging) {
                        drawContent()
                    }
                }
        )

        // 拖拽影子 Icon（跟手）
        if (dragging || offset.value != Offset.Zero) {
            Icon(
                painter = painterResource(Res.drawable.ic_settings),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(APP_HORIZONTAL_DP)
                    .statusBarsPadding()
                    .offset {
                        IntOffset(
                            offset.value.x.roundToInt(),
                            offset.value.y.roundToInt()
                        )
                    }
            )
        }
    }
}