package com.xah.navigation.model.anim.effect

import androidx.compose.runtime.Immutable
import com.xah.navigation.model.anim.effect.PageEffect

@Immutable
data class ForegroundPageEffectState(
    override val effect: PageEffect
) : BasePageEffectState(effect)