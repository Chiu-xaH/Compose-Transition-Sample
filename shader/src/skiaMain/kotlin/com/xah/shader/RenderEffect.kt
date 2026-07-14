package com.xah.shader

import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.asComposeRenderEffect
import org.jetbrains.skia.ImageFilter

actual fun RenderEffect?.chain(other: RenderEffect): RenderEffect {
    return if (this != null) {
        ImageFilter.makeCompose(
            other.asSkiaImageFilter(),
            this.asSkiaImageFilter()
        ).asComposeRenderEffect()
    } else {
        other
    }
}

actual fun RuntimeShaderEffect(
    runtimeShader: RuntimeShader,
    uniformShaderName: String
): RenderEffect {
    return ImageFilter.makeRuntimeShader(
        runtimeShader.asSkiaRuntimeShader(),
        uniformShaderName,
        null
    ).asComposeRenderEffect()
}
