package com.xah.transition.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.xah.shader.state.rememberShaderState
import com.xah.transition.ui.holder.GlobalUiStateHolder

@Composable
fun GlobalShaderStateInit() {
    val state = rememberShaderState()
    LaunchedEffect(state) {
        GlobalUiStateHolder.shaderState = state
    }
}
