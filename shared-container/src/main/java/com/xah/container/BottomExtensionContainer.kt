package com.xah.container

import android.graphics.RenderEffect
import android.graphics.RuntimeShader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import org.intellij.lang.annotations.Language


@Composable
fun BottomExtensionContainer(
    extensionHeight: Float,
    modifier : Modifier = Modifier,
    content : @Composable () -> Unit
) {
    val graphicsLayer = rememberGraphicsLayer()
    var rect by remember { mutableStateOf<Rect?>(null) }

    Column(modifier = modifier) {
        Box(modifier = Modifier
            .drawWithContent {
                drawContent()
                graphicsLayer.record {
                    val bounds = rect ?: return@record
                    withTransform({
                        clipRect(0f, 0f, bounds.width, bounds.height)
                    }) {
                        this@drawWithContent.drawContent()
                    }
                }
            }
            .onGloballyPositioned { layoutCoordinates ->
                rect = layoutCoordinates.boundsInRoot()
            }
        ) {
            content()
        }
        // 放置延展内容
        Box(
            modifier = Modifier
                .bottomExtension(extensionHeight,graphicsLayer,rect)
        )
    }
}


private fun Modifier.bottomExtension(
    extensionHeight: Float,
    parentGraphicsLayer: GraphicsLayer,
    parentRect: Rect?,
    defaultColor : Color = Color.Black
): Modifier {
    if(parentRect == null) {
        return this
    }
    return composed {
        val density = LocalDensity.current

        if (Build.VERSION.SDK_INT < 33) {
            // 使用defaultColor延展填充extensionHeight
            this.background(defaultColor)
        } else {
            // 底部1像素延展填充extensionHeight

            val customRenderEffect = remember(parentRect) {
                val runtimeShader = RuntimeShader(SHADER_CODE.trimIndent())
                runtimeShader.setFloatUniform("size", parentRect.width, parentRect.height)
                runtimeShader.setFloatUniform("extensionHeight", extensionHeight)

                RenderEffect.createRuntimeShaderEffect(runtimeShader, "content").asComposeRenderEffect()
            }

            this.drawWithCache {
                onDrawWithContent {
                    // 使用缓存的自定义渲染效果
                    parentGraphicsLayer.renderEffect = customRenderEffect
                    withTransform({
                        clipRect(0f, 0f, parentRect.width,extensionHeight)
                    }) {
                        drawLayer(parentGraphicsLayer)
                    }
                }
            }
        }
            .size(
                width = with(density) { parentRect.width.toDp() },
                height = with(density) { extensionHeight.toDp() }
            )
    }
}

@Language("AGSL")
private const val SHADER_CODE = """
    uniform shader content;
    uniform float2 size;           // 原图宽高
    uniform float extensionHeight; // 延展高度

    half4 main(float2 fragCoord) {
        // 始终采样底部 1 像素行
        float2 bottomCoord = float2(fragCoord.x, size.y - 1.0);

        return content.eval(bottomCoord);
    }
"""

