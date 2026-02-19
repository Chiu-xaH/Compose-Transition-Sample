package com.xah.container.ui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.lerp
import com.xah.common.util.ScreenCornerHelper
import com.xah.container.ui.util.LocalSharedContainerRegistry
import kotlin.math.roundToInt
import androidx.compose.ui.unit.lerp

@Composable
fun SharedContainerOverlay() {
    val registry = LocalSharedContainerRegistry.current
    val density = LocalDensity.current

    registry.runningStates.forEach { state ->
        val progress = state.animation.value

        if (state.containerRect != null && state.contentRect != null) {
            val container = state.containerRect!!
            val content = state.contentRect!!

            val safelyProgress =  (progress*registry.speedUpRadio).coerceIn(0f,1f)

            val left = lerp(container.left, content.left, progress)
            val top = lerp(container.top, content.top, progress)
            val width = lerp(container.width, content.width, progress)
            val height = lerp(container.height, content.height, progress)

            val contentAlpha = lerp(0f,1f,safelyProgress)
            val corner = lerp(state.containerCorner, ScreenCornerHelper.corner, safelyProgress)


            Box(
                modifier = Modifier
                    .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                    .size(
                        with(density) { width.toDp() },
                        with(density) { height.toDp() }
                    )
                    .clip(RoundedCornerShape(corner))
            ) {
                // 容器及其颜色填充
                Box(
                    modifier = Modifier.background(state.containerColor)
                ) {
                    // content.width * scale = lerp.width
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(modifier = Modifier.align(Alignment.TopCenter)) {
                            Box(
                                modifier = Modifier.graphicsLayer {
                                    val scale = width / container.width
                                    scaleX = scale
                                    scaleY = scale
                                    transformOrigin = TransformOrigin(0.5f, 0f)
                                }
                            ) {
                                state.containerLayout?.let { it() }
                            }
                        }
                    }
                }

                // 内容始终透明度淡入淡出
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = contentAlpha
                        }
                ) {
                    state.contentLayout?.let { it() }
                }
            }
        }
    }
}
