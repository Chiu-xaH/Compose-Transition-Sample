package com.xah.floating.model.anim

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.runtime.Immutable

@Immutable
data class BackgroundEffect(
    val pageEffect : PageEffect,
    val animationSpec: AnimationSpec<Float>
)