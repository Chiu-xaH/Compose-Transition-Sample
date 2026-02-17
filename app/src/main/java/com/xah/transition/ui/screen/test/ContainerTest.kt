package com.xah.transition.ui.screen.test

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class ShareContainerState {
//    var
}

@Preview
@Composable
fun ContainerTest() {
    var displayA by remember { mutableStateOf(true) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    var rectA by remember { mutableStateOf<Rect?>(null) }
    var rectB by remember { mutableStateOf<Rect?>(null) }

    val scope = rememberCoroutineScope()
    var animating by remember { mutableStateOf(false) }
    val anim = remember { Animatable(0f) }

    val density = LocalDensity.current

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        if (animating && rectA != null && rectB != null) {

            val s = rectA!!
            val e = rectB!!
            val p = anim.value

            val left = lerp(s.left, e.left, p)
            val top = lerp(s.top, e.top, p)
            val width = lerp(s.width, e.width, p)
            val height = lerp(s.height, e.height, p)

            Box(
                modifier = Modifier
                    .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                    .size(
                        with(density) { width.toDp() },
                        with(density) { height.toDp() }
                    )
                    .background(Color.Blue)
            )
        }

        if (!displayA) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(80.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Green)
                    .clickable {
                        if (rectB == null) return@clickable
                        displayA = true

                        scope.launch {
                            awaitFrame()  // 等 A layout

                            if (rectA == null) return@launch

                            animating = true
                            anim.animateTo(0f)
                            animating = false
                        }
                    }
                    .onGloballyPositioned {
                        rectB = it.boundsInRoot()
                    }
            )
        }

        if (displayA) {
            Box(
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
                    .size(100.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(Color.Red)
                    .clickable {
                        if (rectA == null) return@clickable
                        displayA = false

                        scope.launch {
                            awaitFrame()  // 等 B layout

                            if (rectB == null) return@launch

                            animating = true
                            anim.animateTo(1f)
                            animating = false
                        }
                    }
                    .onGloballyPositioned {
                        rectA = it.boundsInRoot()
                    }
            )
        }
    }
}

