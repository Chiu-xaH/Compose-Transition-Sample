package com.xah.shader.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import com.xah.shader.skia.RuntimeShader
import com.xah.shader.skia.RuntimeShaderEffect

@Composable
fun rememberShaderState(): ShaderState {
    val graphicsLayer = rememberGraphicsLayer()
    return rememberShaderState(graphicsLayer)
}

@Composable
fun rememberShaderState(
    graphicsLayer : GraphicsLayer
): ShaderState {
    return remember(graphicsLayer) {
        ShaderState(graphicsLayer)
    }
}

class ShaderState internal constructor(
    internal val graphicsLayer: GraphicsLayer,
) {
    // 裁剪形状
    internal var rect: Rect? by mutableStateOf(null)
}

// 记录内容
fun Modifier.shaderSource(
    state : ShaderState
) : Modifier =
    this
        .drawWithContent {
            drawContent()
            state.graphicsLayer.record {
                val bounds = state.rect ?: return@record
                withTransform({
                    // 录全屏
                    clipRect(0f, 0f, bounds.width, bounds.height)
                }) {
                    this@drawWithContent.drawContent()
                }
            }
        }
        .onGloballyPositioned { layoutCoordinates ->
            state.rect = layoutCoordinates.boundsInRoot()
        }


fun Modifier.recordPosition(
    onResult : (Rect) -> Unit
) = this.onGloballyPositioned { layoutCoordinates ->
    val pos = layoutCoordinates.positionInWindow()
    val size = layoutCoordinates.size
    onResult(Rect(
        pos.x,
        pos.y,
        pos.x + size.width,
        pos.y + size.height
    ))
}

// 自定义效果
fun Modifier.shaderLayer(
    state: ShaderState,
    renderEffect : RenderEffect?,
) : Modifier = composed {
    val localLayer = rememberGraphicsLayer()
    var rect by remember { mutableStateOf<Rect?>(null) }

    this
        .drawWithCache {
            onDrawWithContent {
                localLayer.apply {
                    val contentRect = state.rect ?: return@apply
                    val surfaceRect = rect ?: return@apply
                    val offset = surfaceRect.topLeft - contentRect.topLeft

                    record {
                        withTransform({
                            translate(-offset.x, -offset.y)
                        }) {
                            drawLayer(state.graphicsLayer)
                        }
                    }
                }
                localLayer.renderEffect = renderEffect
                rect?.let {
                    withTransform({
                        clipRect(0f, 0f, it.width, it.height)
                    }) {
                        // 裁切录制的内容
                        drawLayer(localLayer)
                    }
                }
                // 原内容
                drawContent()
            }
        }
        // 记录位置
        .recordPosition {
            rect = it
        }
}