package com.xah.shader

import androidx.compose.ui.graphics.RenderEffect

expect fun RenderEffect?.chain(other: RenderEffect): RenderEffect

expect fun RuntimeShaderEffect(
    runtimeShader: RuntimeShader,
    uniformShaderName: String
): RenderEffect