package com.xah.floating.model.anim

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Immutable

@Immutable
data class ForegroundEffect(
    val enter : EnterTransition,
    val exit : ExitTransition
)