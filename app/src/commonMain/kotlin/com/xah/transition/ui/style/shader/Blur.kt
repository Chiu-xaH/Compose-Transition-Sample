package com.xah.transition.ui.style.shader

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import com.sharednav.common.helper.EnableHelper
import com.sharednav.common.modifier.mask
import com.xah.shader.chain

// 层级模糊

// 绘制内容
fun Modifier.blurLayer(
    state: ShaderState,
    tint : Color,
) : Modifier = composed {
    var rect by remember { mutableStateOf<Rect?>(null) }

    this
        .mask(color = tint)
        .drawWithCache {
            onDrawBehind {
                // 绘制
                val contentRect = state.rect ?: return@onDrawBehind
                val surfaceRect = rect ?: return@onDrawBehind

                val offset = surfaceRect.topLeft - contentRect.topLeft
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
fun Modifier.blurLayer(
    state: ShaderState,
) : Modifier = composed {
    var rect by remember { mutableStateOf<Rect?>(null) }

    this
        .drawWithCache {
            onDrawBehind {
                // 绘制
                val contentRect = state.rect ?: return@onDrawBehind
                val surfaceRect = rect ?: return@onDrawBehind

                val offset = surfaceRect.topLeft - contentRect.topLeft
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

// 记录内容
fun Modifier.blurSource(
    state : ShaderState,
    blur : Dp,
    enabled : Boolean = true,
    enhanceColor : Boolean = false,
) : Modifier =
    if(!(enabled && EnableHelper.canBlur))
        this
    else
        this
            .drawWithContent {
                drawContent()

                val blurEffect = BlurEffect(blur.toPx(), blur.toPx(), TileMode.Clamp)
                val enhanceEffect = enhanceColorShader(enhanceColor)
                val chained = enhanceEffect.chain(blurEffect)

                state.graphicsLayer.renderEffect = chained
                // 模糊后的画面录制下拉
                state.graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
            }
            .onGloballyPositioned { layoutCoordinates ->
                state.rect = layoutCoordinates.boundsInRoot()
            }

