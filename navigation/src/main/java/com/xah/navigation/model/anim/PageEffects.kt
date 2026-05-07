package com.xah.navigation.model.anim

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

@Immutable
data class PageEffects(
    val backgroundEffect : PageEffectState,
    val foregroundEffect : PageEffectState,
) {
    /*
     * 动画分级
     */

    fun background(progress : Float,level: EffectLevel) =
        backgroundEffect
            .lerp(progress)
            .let {
                when(level) {
                    EffectLevel.FULL -> {
                        it
                    }
                    EffectLevel.NO_BLUR -> {
                        it.copy(blur = 0.dp)
                    }
                    EffectLevel.NO_SCALE -> {
                        it.copy(blur = 0.dp, scale = 1f)
                    }
                    EffectLevel.NONE -> {
                        it.copy(blur = 0.dp, scale = 1f)
                    }
                }
            }

    fun foreground(progress : Float,level: EffectLevel) =
        foregroundEffect
            .let {
                when(level) {
                    EffectLevel.FULL -> {
                        it.lerp(progress)
                    }
                    EffectLevel.NO_BLUR -> {
                        it.lerp(progress).copy(blur = 0.dp)
                    }
                    EffectLevel.NO_SCALE -> {
                        it.lerp(progress).copy(blur = 0.dp)
                    }
                    EffectLevel.NONE -> {
                        PageEffect(
                            scale = lerp(backgroundEffect.end.scale,1f,progress),
                            blur = 0.dp,
                            mask = lerp(it.start.mask,it.end.mask,progress),
                            corner = it.end.corner,
                            alpha = lerp(0f,1f,progress),
                        )
                    }
                }
            }
}
