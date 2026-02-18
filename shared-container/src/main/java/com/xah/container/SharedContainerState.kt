package com.xah.container

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.util.lerp

class SharedContainerState() {
    var layoutRect by mutableStateOf<Rect?>(null)

    var transitionFrom by mutableStateOf<Rect?>(null)
    var transitionTo by mutableStateOf<Rect?>(null)

    val animation = Animatable(0f)
    var isRunning by mutableStateOf(false)

    fun currentVisualRect(): Rect? {
        val from = transitionFrom
        val to = transitionTo
        val p = animation.value

        return when {
            from != null && to != null -> Rect(
                lerp(from.left, to.left, p),
                lerp(from.top, to.top, p),
                lerp(from.right, to.right, p),
                lerp(from.bottom, to.bottom, p)
            )
            else -> layoutRect
        }
    }

}
