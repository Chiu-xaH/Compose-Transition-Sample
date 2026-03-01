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
 * @param isLandscape 为true则取右侧1像素，否则取底部1像素
 */
fun Modifier.bottomExtension(
    parentGraphicsLayer: GraphicsLayer,
    parentRect: Rect?,
    isLandscape : Boolean,
): Modifier {
    if(parentRect == null) {
        return this
    }
    return composed {
        if (Build.VERSION.SDK_INT < 33) {
            // 使用defaultColor延展填充extensionHeight
            this
        } else {
            // 底部1像素延展填充
            val customRenderEffect = remember(parentRect) {
                val runtimeShader = RuntimeShader(
                    if(isLandscape) {
                        END_SHADER_CODE
                    } else {
                        BOTTOM_SHADER_CODE
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
private const val BOTTOM_SHADER_CODE = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {
        // 采样底部1像素行
        float2 bottomCoord = float2(fragCoord.x, size.y - 1.0);

        return content.eval(bottomCoord);
    }
"""

@Language("AGSL")
private const val END_SHADER_CODE = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {
        // 采样右侧1像素列
        float2 bottomCoord = float2(size.x - 1.0, fragCoord.y);

        return content.eval(bottomCoord);
    }
"""

