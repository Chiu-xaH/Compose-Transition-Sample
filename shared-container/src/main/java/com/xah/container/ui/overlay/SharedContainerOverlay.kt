package com.xah.container.ui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import com.xah.common.ScreenCornerHelper
import com.xah.common.disableTouchEvent
import com.xah.container.logic.ContainerFilledStrategy
import com.xah.container.ui.container.bottomExtension
import com.xah.container.ui.util.LocalSharedContainerRegistry
import kotlin.math.roundToInt

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

            val rect = registry.rectInterpolator(progress, container, content)
            val left = rect.left
            val top = rect.top
            val width = rect.width
            val height = rect.height

            val contentAlpha = lerp(0f,1f,safelyProgress)
            val corner = lerp(state.containerCorner, ScreenCornerHelper.corner, safelyProgress)

            val containerFilledStrategy = state.containerFilledStrategy.getFinalStrategy()
            val graphicsLayer = rememberGraphicsLayer()

            Box(
                modifier = Modifier
                    .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                    .size(
                        with(density) { width.toDp() },
                        with(density) { height.toDp() }
                    )
                    .clip(RoundedCornerShape(corner))
                    // is ContainerFilledStrategy.Color
                    .background((containerFilledStrategy as? ContainerFilledStrategy.Color)?.color ?: Color.Black)
            ) {
                // 容器
                Column {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.align(Alignment.TopCenter)) {
                            if(containerFilledStrategy is ContainerFilledStrategy.Clip) {
                                // 对state.containerLayout竖直裁切填满父容器
                                Box(
                                    modifier = Modifier
                                        .graphicsLayer {
                                            val scale = height / container.height
                                            scaleX = scale
                                            scaleY = scale
                                            transformOrigin = TransformOrigin(0.5f, 0f)
                                        }
                                ) {
                                    // 背景禁用触摸事件
                                    Box(modifier = Modifier.disableTouchEvent()) {
                                        state.containerLayout?.let { it() }
                                    }
                                }
                            } else {
                                // 底部填充
                                Box(
                                    modifier = Modifier.graphicsLayer {
                                        val scale = width / container.width
                                        scaleX = scale
                                        scaleY = scale
                                        transformOrigin = TransformOrigin(0.5f, 0f)
                                    }
                                ) {
                                    Box(modifier = Modifier
                                        .drawWithContent {
                                            drawContent()
                                            if(containerFilledStrategy is ContainerFilledStrategy.Pixel) {
                                                graphicsLayer.record {
                                                    this@drawWithContent.drawContent()
                                                }
                                            }
                                        }
                                    ) {
                                        // 背景禁用触摸事件
                                        Box(modifier = Modifier.disableTouchEvent()) {
                                            state.containerLayout?.let { it() }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    // 使用延展填充
                    if(containerFilledStrategy is ContainerFilledStrategy.Pixel) {
                        Box(
                            modifier = Modifier
                                .zIndex(-1f)
                                .graphicsLayer {
                                    val scale = width / container.width
                                    scaleX = scale
                                    scaleY = scale
                                    transformOrigin = TransformOrigin(0.5f, 0f)
                                }
                                .bottomExtension(graphicsLayer,container)
                        )
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
