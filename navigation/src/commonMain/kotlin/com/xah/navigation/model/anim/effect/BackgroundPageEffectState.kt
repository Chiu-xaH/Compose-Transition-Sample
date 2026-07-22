package com.xah.navigation.model.anim.effect

import androidx.compose.runtime.Immutable
import com.xah.navigation.model.anim.effect.sub.BgEffectBackground

@Immutable
data class BackgroundPageEffectState(
    val enableMirror : Boolean,
    override val effect: PageEffect,
    val backgroundColor : BgEffectBackground? = null,
) : BasePageEffectState(effect)