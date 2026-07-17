package com.xah.navigation.anim.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.navigation.model.anim.effect.PageEffects
import com.xah.navigation.model.anim.effect.sub.BgEffectBackground
import kotlin.invoke

@Composable
actual fun rememberJumpPageEffects(
    background: BgEffectBackground,
    enableAlpha: Boolean
): PageEffects = JumpPageEffects(background,enableAlpha)