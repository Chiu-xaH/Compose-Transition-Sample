package com.xah.navigation.anim.effect

import androidx.compose.runtime.Composable
import com.xah.navigation.model.anim.effect.PageEffects

@Composable
actual fun rememberRollPageEffects(
    direction: Direction,
    clip: Boolean,
): PageEffects = RollPageEffects(direction, clip)
