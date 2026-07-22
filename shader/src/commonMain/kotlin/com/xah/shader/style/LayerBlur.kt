package com.xah.shader.style

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.unit.Dp
import com.xah.shader.skia.BlurRenderEffect
import com.xah.shader.state.ShaderState
import com.xah.shader.skia.canUseBlurRenderEffect
import com.xah.shader.state.recordPosition

// 层级模糊
fun Modifier.blurLayer(
    state: ShaderState,
    blur : Dp,
) : Modifier = composed {
    var rect by remember { mutableStateOf<Rect?>(null) }

    this
        .drawWithCache {
            onDrawBehind {
                // 绘制
                val contentRect = state.rect ?: return@onDrawBehind
                val surfaceRect = rect ?: return@onDrawBehind
                val offset = surfaceRect.topLeft - contentRect.topLeft

                val blurEffect = BlurRenderEffect(blur.toPx())
                state.graphicsLayer.renderEffect = blurEffect

                withTransform({
                    translate(-offset.x, -offset.y)
                }) {
                    drawLayer(state.graphicsLayer)
                }
            }
        }
        .recordPosition {
            rect = it
        }
}
/*
fun Modifier.blurLayer(
    state: ShaderState,
    blur : Dp,
) : Modifier = composed {
    val density = LocalDensity.current
    var rect by remember { mutableStateOf<Rect?>(null) }
    val effect = with(density) {
        BlurEffect(blur.toPx(), blur.toPx())
    }
    this.shaderLayer(state, renderEffect = effect, overlayColor = Color.Transparent,rect = rect) {
        rect = it
    }
}
 */

// 记录内容
fun Modifier.blurSource(
    state : ShaderState
) : Modifier =
    if(!canUseBlurRenderEffect)
        this
    else
        this
            .drawWithContent {
                drawContent()
                state.graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
            }
            .recordPosition {
                state.rect = it
            }

/*
fun Modifier.blurSource(
    state : ShaderState
) : Modifier =
    this.shaderSource(state)
 */