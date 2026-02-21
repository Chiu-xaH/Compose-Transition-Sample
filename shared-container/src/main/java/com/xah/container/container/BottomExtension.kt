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

fun Modifier.bottomExtension(
    parentGraphicsLayer: GraphicsLayer,
    parentRect: Rect?,
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
                val runtimeShader = RuntimeShader(SHADER_CODE.trimIndent())
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
private const val SHADER_CODE = """
    uniform shader content;
    uniform float2 size;          

    half4 main(float2 fragCoord) {
        // 始终采样底部1像素行
        float2 bottomCoord = float2(fragCoord.x, size.y - 1.0);

        return content.eval(bottomCoord);
    }
"""

