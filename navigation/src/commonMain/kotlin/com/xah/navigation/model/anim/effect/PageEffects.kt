package com.xah.navigation.model.anim.effect

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.dp
import com.xah.navigation.anim.effect.DefaultLevelNoneTransitionEffect
import com.xah.navigation.model.anim.EffectLevel

@Immutable
open class PageEffects(
    val backgroundEffect : BackgroundPageEffectState,
    val foregroundEffect : ForegroundPageEffectState,
) {

    /**
     * 动画分级，自定义效果时可重写，无需处理NONE的情况
     */
    open fun background(progress : Float,level: EffectLevel) =
        backgroundEffect
            .lerp(progress)
            .let {
                when(level) {
                    EffectLevel.MEDIUM -> {
                        it.copy(blur = 0.dp, innerBlur = 0.dp)
                    }
                    EffectLevel.LOW -> {
                        it.copy(blur = 0.dp, innerBlur = 0.dp, scale = 1f)
                    }
                    else -> {
                        it
                    }
                }
            }

    /**
     * 动画分级，自定义效果时可重写，无需处理NONE的情况
     */
    open fun foreground(progress : Float,level: EffectLevel) =
        foregroundEffect
            .let {
                when(level) {
                    EffectLevel.MEDIUM -> {
                        it.lerp(progress).copy(blur = 0.dp, innerBlur = 0.dp)
                    }
                    EffectLevel.LOW -> {
                        it.lerp(progress).copy(blur = 0.dp, innerBlur = 0.dp)
                    }
                    else -> {
                        it.lerp(progress)
                    }
                }
            }
}