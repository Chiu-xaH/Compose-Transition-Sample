package com.xah.navigation.anim.effect

import androidx.compose.runtime.Composable
import com.xah.navigation.model.anim.effect.PageEffects

@Composable
actual fun rememberSlidePageEffects(
    direction: Direction,
    clip: Boolean
): PageEffects = SlidePageEffects(direction,clip)