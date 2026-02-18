package com.xah.container.logic

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.util.lerp

class SharedContainerState() {
    var layoutRect by mutableStateOf<Rect?>(null)

    var rectFrom by mutableStateOf<Rect?>(null)
    var rectTo by mutableStateOf<Rect?>(null)

    var targetRect by mutableStateOf<Rect?>(null)

    val animation = Animatable(0f)
    var isRunning by mutableStateOf(false)

    var content: (@Composable () -> Unit)? = null
}