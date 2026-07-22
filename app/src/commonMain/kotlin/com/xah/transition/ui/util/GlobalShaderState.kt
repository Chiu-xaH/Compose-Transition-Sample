package com.xah.transition.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.xah.shader.state.ShaderState
import com.xah.shader.state.rememberShaderState

object GlobalShaderState {
    var shaderState by mutableStateOf<ShaderState?>(null)
}

@Composable
fun GlobalShaderStateInit() {
    val state = rememberShaderState()
    LaunchedEffect(state) {
        GlobalShaderState.shaderState = state
    }
}
