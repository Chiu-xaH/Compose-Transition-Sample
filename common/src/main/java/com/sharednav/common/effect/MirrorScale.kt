package com.sharednav.common.effect

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import org.intellij.lang.annotations.Language

fun Modifier.scaleMirror(scale: Float,enabled : Boolean): Modifier =
    if(!enabled || Build.VERSION.SDK_INT < 33) {
        this.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    } else {
        composed {
            // 绘制面
            var rect by remember { mutableStateOf<Rect?>(null) }
            // shader 只在尺寸变化时重建，不随每帧 scale 变化重建
            val shader = remember(rect) {
                rect?.let { r ->
                    RuntimeShader(SHADER_CODE.trimIndent()).also {
                        it.setFloatUniform("size", r.width, r.height)
                    }
                }
            }

            this
                .graphicsLayer {
                    clip = true
                    shape = RectangleShape
                    shader?.let { s ->
                        // 每帧只更新 scale uniform，不重建 shader/RenderEffect
                        s.setFloatUniform("scale", scale)
                        renderEffect = RenderEffect.createRuntimeShaderEffect(s, "content").asComposeRenderEffect()
                    }
                }
                .onGloballyPositioned { layoutCoordinates ->
                    val pos = layoutCoordinates.positionInWindow()
                    val size = layoutCoordinates.size
                    rect = Rect(
                        pos.x,
                        pos.y,
                        pos.x + size.width,
                        pos.y + size.height
                    )
                }
        }
    }


@Language("agsl")
private const val SHADER_CODE = """
    uniform shader content;
    uniform float2 size;   // 原始画面宽高
    uniform float scale;   // 缩放比例，
        
    half4 main(float2 fragCoord) {
        float2 center = size * 0.5;
        float2 offset = fragCoord - center;
        
        // 缩放
        float2 scaled = offset / scale;
        float2 sampleCoord = center + scaled;
        
        // 镜面反射逻辑
        if(sampleCoord.x < 0.0) sampleCoord.x = -sampleCoord.x;
        if(sampleCoord.x > size.x) sampleCoord.x = 2.0*size.x - sampleCoord.x;
        
        if(sampleCoord.y < 0.0) sampleCoord.y = -sampleCoord.y;
        if(sampleCoord.y > size.y) sampleCoord.y = 2.0*size.y - sampleCoord.y;
        
        return content.eval(sampleCoord);
    }
"""

