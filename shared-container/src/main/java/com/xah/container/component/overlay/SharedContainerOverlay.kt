package com.xah.container.component.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import com.sharednav.common.util.lerp
import com.xah.container.anim.LinearRectInterpolator
import com.xah.container.util.pixelExtension
import com.xah.container.model.ContainerFilledStrategy
import com.xah.container.model.ContentStrategy
import com.xah.container.util.LocalSharedRegistry
import kotlin.math.abs
import kotlin.math.sign

@Composable
fun SharedContainerOverlay() {
    val registry = LocalSharedRegistry.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp
    val extensionDouble = registry.extensionDouble

    registry.runningStates.forEach { state ->
        key(state) {
            val container = state.containerRect!!
            val originContent = state.contentRect!!
            val content = originContent.copy(
                left = state.contentOffset.x + originContent.left,
                right = state.contentOffset.x + originContent.right,
                bottom = state.contentOffset.y + originContent.bottom,
                top = state.contentOffset.y + originContent.top
            )

            val useContainer = state.contentStrategy !is ContentStrategy.Copy
            val useLinear = (state.contentStrategy as? ContentStrategy.Layer)?.isFloating == false

            // 进度
            val progress = state.animation.value
            val safelyProgress = (progress * registry.speedUpRadio * if(useContainer) 1f else 2f).coerceIn(0f,1f)

            // 路径曲线
            val parent = (
                if(useLinear || !useContainer) {
                    LinearRectInterpolator
                } else {
                    registry.FullScreenRectInterpolator
                }
            ).invoke(progress, container, content)

            val contentAlpha = lerp(0f,1f,safelyProgress)
            val corner = lerp(state.containerCorner,state.contentCorner,safelyProgress)

            // 倾斜计算
            val maxTilt = registry.tiltMaxValue
            val (roX, roY) = if (registry.enableTilt && !useLinear) {

                val cCenterX = container.left + container.width / 2f
                val cCenterY = container.top + container.height / 2f

                val tCenterX = content.left + content.width / 2f
                val tCenterY = content.top + content.height / 2f

                val dx = cCenterX - tCenterX
                val dy = cCenterY - tCenterY

                val dirY = when {
                    dx > 0 -> 1f
                    dx < 0 -> -1f
                    else -> 0f
                }

                val dirX = when {
                    dy > 0 -> -1f
                    dy < 0 -> 1f
                    else -> 0f
                }

                val dxNorm = (dx / content.width).coerceIn(-1f, 1f)
                val dyNorm = (dy / content.height).coerceIn(-1f, 1f)

                val widthDelta = abs(content.width - container.width)
                val heightDelta = abs(content.height - container.height)

                val widthFactor = 1f - (1f - (widthDelta / content.width).coerceIn(0f, 1f)).let { it * it }
                val heightFactor = 1f - (1f - (heightDelta / content.height).coerceIn(0f, 1f)).let { it * it }

                val tiltStrengthX1 = abs(maxTilt * heightFactor * dyNorm)
                val tiltStrengthY1 = abs(maxTilt * widthFactor * dxNorm)

                val tiltStrengthX2 = abs(maxTilt * widthFactor * sign(dx))
                val tiltStrengthY2 = abs(maxTilt * heightFactor * sign(dy))

                val tiltStrengthX = minOf(tiltStrengthX1, tiltStrengthX2)
                val tiltStrengthY = minOf(tiltStrengthY1, tiltStrengthY2)

                val currentTilt = 1f - abs(2f * safelyProgress - 1f)

                Pair(
                    dirX * currentTilt * tiltStrengthX,
                    dirY * currentTilt * tiltStrengthY
                )

            } else {
                Pair(0f, 0f)
            }

            // 填充策略
            val containerFilledStrategy = state.containerFilledStrategy.getFinalStrategy(registry.enableShader)
            val heightW = container.height / content.height
            val widthW = container.width / content.width
            val isHorizontal = if(heightW > widthW) {
                // 左右填充
                true
            } else if(heightW < widthW) {
                // 上下填充
                false
            } else {
                isLandscape
            }

            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = parent.left
                        translationY = parent.top

                        rotationX = roX
                        rotationY = roY
                    }
                    .size(
                        with(density) { parent.width.toDp() },
                        with(density) { parent.height.toDp() }
                    )
                    .clip(corner)
                    .let {
                        // 如果出现了边界空缺，说明SharedContainer里面的Content可能不是0圆角的矩形，导致取像素、裁切出现空缺，请把圆角裁剪挪到SharedContainer的corner参数中，里面的内容不要裁切任何圆角！
                        if(containerFilledStrategy is ContainerFilledStrategy.Color && useContainer) {
                            it.background(containerFilledStrategy.color)
                        } else {
                            it
                        }
                    }
            ) {
                if(useContainer) {
                    // 容器
                    Box {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Box(modifier = Modifier.align(
                                if(isHorizontal) {
                                    if(containerFilledStrategy is ContainerFilledStrategy.Clip) {
                                        Alignment.CenterStart
                                    } else {
                                        if(!extensionDouble) {
                                            Alignment.TopStart
                                        } else {
                                            Alignment.TopCenter
                                        }
                                    }
                                } else {
                                    if(containerFilledStrategy is ContainerFilledStrategy.Clip) {
                                        Alignment.TopCenter
                                    } else {
                                        if(!extensionDouble) {
                                            Alignment.TopCenter
                                        } else {
                                            Alignment.CenterStart
                                        }
                                    }
                                }
                            )) {
                                when(containerFilledStrategy) {
                                    is ContainerFilledStrategy.Clip -> {
                                        Box(modifier = Modifier
                                            .drawWithCache {
                                                onDrawWithContent {
                                                    val layer = state.containerLayer ?: return@onDrawWithContent
                                                    val scale = if(isHorizontal) {
                                                        parent.width / container.width
                                                    } else {
                                                        parent.height / container.height
                                                    }
                                                    withTransform({
                                                        scale(scale, scale)
                                                        if(isHorizontal) {
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
                                    else -> {
                                        // 填充
                                        Box(modifier = Modifier
                                            .drawWithCache {
                                                onDrawWithContent {
                                                    val layer = state.containerLayer ?: return@onDrawWithContent
                                                    val scale = if(!isHorizontal) {
                                                        parent.width / container.width
                                                    } else {
                                                        parent.height / container.height
                                                    }
                                                    withTransform({
                                                        scale(scale, scale)
                                                        if(!extensionDouble) {
                                                            if(isHorizontal) {
                                                                translate(left = 0f , top = 0f)
                                                            } else {
                                                                translate(left = -container.width/2f , top = 0f)
                                                            }
                                                        } else {
                                                            if(isHorizontal) {
                                                                translate(left = -container.width/2 , top = 0f)
                                                            } else {
                                                                translate(left = 0f , top = -container.height/2f)
                                                            }
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
                            state.containerLayerForPixel?.let { layer ->
                                Box(
                                    modifier = Modifier
                                        .zIndex(-1f)
                                        .graphicsLayer {
                                            val scale = if(!isHorizontal) {
                                                parent.width / container.width
                                            } else {
                                                parent.height / container.height
                                            }
                                            scaleX = scale
                                            scaleY = scale
                                        }
                                        .pixelExtension(layer,container,isHorizontal,extensionDouble)
                                )
                            }
                        }
                    }
                }
                // 内容始终透明度淡入淡出
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.align(
                        if(extensionDouble) {
                            Alignment.Center
                        } else {
                            Alignment.TopStart
                        }
                    )) {
                        Box(
                            modifier = Modifier.drawWithCache {
                                onDrawWithContent {
                                    val layer = state.contentLayer ?: return@onDrawWithContent
                                    val scale = if(isHorizontal) {
                                        parent.height / content.height
                                    } else {
                                        parent.width / content.width
                                    }

                                    withTransform({
                                        scale(scale, scale)
                                        if(extensionDouble) {
                                            translate(left = -content.width/2f , top = -content.height/2f)
                                        } else {
                                            translate(left = 0f, top = 0f)
                                        }
                                    }) {
                                        layer.alpha = contentAlpha
                                        drawLayer(layer)
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

