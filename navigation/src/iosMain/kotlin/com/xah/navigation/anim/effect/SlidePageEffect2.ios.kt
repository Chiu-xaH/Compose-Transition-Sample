package com.xah.navigation.anim.effect

import androidx.compose.runtime.Composable
import com.xah.navigation.model.anim.effect.PageEffects

@Composable
actual fun rememberSlidePageEffects2(
    direction: Direction,
): PageEffects = SlidePageEffects2(direction)