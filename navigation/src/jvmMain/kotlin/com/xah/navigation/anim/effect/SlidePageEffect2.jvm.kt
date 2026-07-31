package com.xah.navigation.anim.effect

import com.xah.navigation.model.anim.effect.PageEffects
import androidx.compose.runtime.Composable

@Composable
actual fun rememberSlidePageEffects2(
    direction: Direction,
): PageEffects = SlidePageEffects2(direction)