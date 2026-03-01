package com.xah.container.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import com.xah.common.disableTouchEvent
import com.xah.container.container.bottomExtension
import com.xah.container.model.ContainerFilledStrategy
import com.xah.container.utils.LocalSharedContainerRegistry
import kotlin.math.roundToInt

@Composable
fun SharedContainerOverlay() {
    val registry = LocalSharedContainerRegistry.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    registry.runningStates.forEach { state ->
        val progress = state.animation.value

        if (state.containerRect != null && state.contentRect != null) {
            val container = state.containerRect!!
            val content = state.contentRect!!

            val safelyProgress =  (progress * registry.speedUpRadio).coerceIn(0f,1f)

            val rect = registry.rectInterpolator(progress, container, content)
            val left = rect.left
            val top = rect.top
            val width = rect.width
            val height = rect.height

            val contentAlpha = lerp(0f,1f,safelyProgress)
            val corner = lerp(state.containerCorner,state.contentCorner,safelyProgress)

            val containerFilledStrategy = state.containerFilledStrategy.getFinalStrategy()

            Box(
                modifier = Modifier
                    .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                    .size(
                        with(density) { width.toDp() },
                        with(density) { height.toDp() }
                    )
                    .clip(corner)
                    .background(
                        when(containerFilledStrategy) {
                            // 如果出现了黑色边界，说明SharedContainer里面的Content可能不是0圆角的矩形，导致取像素、裁切出现空缺，请把圆角裁剪挪到SharedContainer的corner参数中，里面的内容不要裁切任何圆角！
                            is ContainerFilledStrategy.Pixel -> Color.Black
                            is ContainerFilledStrategy.Clip -> Color.Black
                            is ContainerFilledStrategy.Color -> containerFilledStrategy.color
                        }
                    )
            ) {
                // 容器
                Box(modifier = Modifier.disableTouchEvent()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.align(
                            if(isLandscape) {
                                if(containerFilledStrategy is ContainerFilledStrategy.Clip) {
                                    Alignment.CenterStart
                                } else {
                                    Alignment.TopStart
                                }
                            } else {
                                Alignment.TopCenter
                            }
                        )) {
                            when(containerFilledStrategy) {
                                is ContainerFilledStrategy.Clip -> {
                                    // 对state.containerLayout竖直裁切填满父容器
                                    Box(modifier = Modifier
                                        .drawWithCache {
                                            onDrawWithContent {
                                                val layer = state.containerLayer ?: return@onDrawWithContent
                                                val scale = if(isLandscape) {
                                                    width / container.width
                                                } else {
                                                    height / container.height
                                                }
                                                withTransform({
                                                    scale(scale, scale)
                                                    if(isLandscape) {
                                                        translate(left = 0f , top = -container.height/2f)
                                                    } else {
                                                        translate(left = -container.width/2f , top = 0f)
                                                    }
                                                }) {
                                                    drawLayer(layer)
                                                }
                                            }
                                        }
                                    )
                                }
                                is ContainerFilledStrategy.Color -> {
                                    Box(modifier = Modifier
                                        .drawWithCache {
                                            onDrawWithContent {
                                                val layer = state.containerLayer ?: return@onDrawWithContent
                                                val scale = if(!isLandscape) {
                                                    width / container.width
                                                } else {
                                                    height / container.height
                                                }
                                                withTransform({
                                                    scale(scale, scale)
                                                    if(isLandscape) {
                                                        translate(left = 0f , top = 0f)
                                                    } else {
                                                        translate(left = -container.width/2f , top = 0f)
                                                    }
                                                }) {
                                                    drawLayer(layer)
                                                }
                                            }
                                        }
                                    )
                                }
                                is ContainerFilledStrategy.Pixel -> {
                                    // 底部填充
                                    Box(modifier = Modifier
                                        .drawWithCache {
                                            onDrawWithContent {
                                                val layer = state.containerLayer ?: return@onDrawWithContent
                                                val scale = if(!isLandscape) {
                                                    width / container.width
                                                } else {
                                                    height / container.height
                                                }
                                                withTransform({
                                                    scale(scale, scale)
                                                    if(isLandscape) {
                                                        translate(left = 0f , top = 0f)
                                                    } else {
                                                        translate(left = -container.width/2f , top = 0f)
                                                    }
                                                }) {
                                                    drawLayer(layer)
                                                }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                    // 使用延展填充
                    if(containerFilledStrategy is ContainerFilledStrategy.Pixel) {
                        val layer = state.containerLayerForPixel
                        layer?.let {
                            Box(
                                modifier = Modifier
                                    .zIndex(-1f)
                                    .graphicsLayer {
                                        val scale = if(!isLandscape) {
                                            width / container.width
                                        } else {
                                            height / container.height
                                        }
                                        scaleX = scale
                                        scaleY = scale
                                    }
                                    .bottomExtension(it,container,isLandscape)
                            )
                        }
                    }
                }
                // 内容始终透明度淡入淡出
                Box(
                    modifier = Modifier.drawWithContent {
                        val layer = state.contentLayer ?: return@drawWithContent
                        val scale = if(!isLandscape) {
                            width / content.width
                        } else {
                            height / content.height
                        }
                        withTransform({
                            scale(scale, scale)
                        }) {
                            layer.alpha = contentAlpha
                            drawLayer(layer)
                        }
                    }
                )
            }
        }
    }
}

private fun lerp(start: CornerBasedShape, stop: CornerBasedShape, fraction: Float): CornerBasedShape = start.lerp(stop,fraction) as CornerBasedShape
