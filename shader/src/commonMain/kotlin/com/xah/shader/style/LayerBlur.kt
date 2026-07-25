package com.xah.shader.style

import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.unit.Dp
import com.xah.shader.skia.BlurRenderEffect
import com.xah.shader.state.ShaderState
import com.xah.shader.state.shaderLayer

// 层级模糊
fun Modifier.blurLayer(
    state: ShaderState,
    blur : Dp,
) : Modifier = composed {
    val effect = BlurRenderEffect(blur)
    this.shaderLayer(state, renderEffect = effect)
}
