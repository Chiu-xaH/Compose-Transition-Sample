package com.xah.navigation.model.anim.effect.sub

import androidx.compose.runtime.Immutable

@Immutable
data class Roll(
    val top: Float = 0f,
    val bottom: Float = 0f,
    val left: Float = 0f,
    val right: Float = 0f,
) {
    companion object Companion {
        val None = Roll()
    }
}
