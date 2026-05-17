package com.xah.navigation.model.anim

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp

@Immutable
open class PageEffects(
    val backgroundEffect : BackgroundPageEffectState,
    val foregroundEffect : ForegroundPageEffectState,
) {

    /**
     * 动画分级，自定义效果时可重写
     */
    open fun background(progress : Float,level: EffectLevel) =
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

    /**
     * 动画分级，自定义效果时可重写
     */
    open fun foreground(progress : Float,level: EffectLevel) =
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
                        PageEffectFrame(
                            scale = lerp(backgroundEffect.effect.scale.end,1f,progress),
                            blur = 0.dp,
                            mask = lerp(it.effect.mask.start,it.effect.mask.end,progress),
                            corner = it.effect.corner.end,
                            alpha = lerp(0f,1f,progress),
                        )
                    }
                }
            }
}
