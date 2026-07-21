package com.xah.navigation.anim.effect

import androidx.compose.runtime.Composable
import com.xah.navigation.model.anim.effect.PageEffects

@Composable
actual fun rememberRevealPageEffects(
    direction: Direction,
    clip: Boolean,
    offset : Boolean
): PageEffects = RollPageEffects(direction, clip,offset)
