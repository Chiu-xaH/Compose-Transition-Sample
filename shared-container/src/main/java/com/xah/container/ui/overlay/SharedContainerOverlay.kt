package com.xah.container.ui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.lerp
import com.xah.container.ui.util.LocalSharedContainerRegistry
import kotlin.math.roundToInt

// TODO 主内容始终透明度淡入淡出
@Composable
fun SharedContainerOverlay() {
    val registry = LocalSharedContainerRegistry.current
    val density = LocalDensity.current

    registry.runningStates.forEach { state ->
        val content = state.content

        val p = state.animation.value

        if (state.rectFrom != null && state.rectTo != null && content != null) {
            val s = state.rectFrom!!
            val e = state.rectTo!!

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
                    .clipToBounds()
                    .background(Color.Yellow)
            ) {
                // content.width * scale = lerp.width
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(modifier = Modifier.align(Alignment.TopCenter)) {
                        Box(
                            modifier = Modifier.graphicsLayer {
                                val scale = state.targetRect?.width?.let { width / it } ?: 1f
                                scaleX = scale
                                scaleY = scale
                                transformOrigin = TransformOrigin(0.5f, 0f)
                            }
                        ) {
                            content()
                        }
                    }
                }
            }
        }
    }
}
