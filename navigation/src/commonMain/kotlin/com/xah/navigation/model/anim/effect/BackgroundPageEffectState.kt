package com.xah.navigation.model.anim.effect

import androidx.compose.runtime.Immutable
import com.xah.navigation.model.anim.effect.sub.BgEffectBackground

@Immutable
data class BackgroundPageEffectState(
    val enableMirror : Boolean,
    val backgroundColor : BgEffectBackground? = null,
    override val effect: PageEffect
) : BasePageEffectState(effect)