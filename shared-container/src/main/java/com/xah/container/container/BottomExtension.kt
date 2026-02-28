package com.xah.container.container

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import org.intellij.lang.annotations.Language

/**
 * @param useSinglePoint 使用单点取色，取水平居中底部1像素，否则则取完整的底部1像素
 */
fun Modifier.bottomExtension(
    parentGraphicsLayer: GraphicsLayer,
    parentRect: Rect?,
    useSinglePoint : Boolean = false,
): Modifier {
    if(parentRect == null) {
        return this
    }
    return composed {
        if (Build.VERSION.SDK_INT < 33) {
            // 使用defaultColor延展填充extensionHeight
            this
        } else {
            // 底部1像素延展填充extensionHeight
            val customRenderEffect = remember(parentRect) {
                val runtimeShader = RuntimeShader(
                    if(useSinglePoint) {
                        SHADER_CODE_SINGLE_POINT
                    } else {
                        SHADER_CODE
                    }.trimIndent()
                )
                runtimeShader.setFloatUniform("size", parentRect.width, parentRect.height)

                RenderEffect.createRuntimeShaderEffect(runtimeShader, "content").asComposeRenderEffect()
            }

            this.drawWithCache {
                onDrawWithContent {
                    // 使用缓存的自定义渲染效果
                    parentGraphicsLayer.renderEffect = customRenderEffect
                    drawLayer(parentGraphicsLayer)
                }
            }
        }
    }
}

@Language("AGSL")
private const val SHADER_CODE_SINGLE_POINT = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {
        // 获取底部居中点的坐标
        float2 centerCoord = float2(size.x / 2.0, size.y - 1.0);

        return content.eval(centerCoord);
    }
"""

@Language("AGSL")
private const val SHADER_CODE = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {
        // 始终采样底部1像素行
        float2 bottomCoord = float2(fragCoord.x, size.y - 1.0);

        return content.eval(bottomCoord);
    }
"""

