package com.xah.navigation.anim.effect

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.ui.unit.dp
import com.sharednav.common.helper.NoneRoundShape
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.anim.TransitionEffect
import com.xah.navigation.model.anim.effect.BackgroundPageEffectState
import com.xah.navigation.model.anim.effect.EffectValue
import com.xah.navigation.model.anim.effect.ForegroundPageEffectState
import com.xah.navigation.model.anim.effect.PageEffect
import com.xah.navigation.model.anim.effect.PageEffects

/**
 * 缩放
 * eg: 没有eg，聚在工大以前用过这个动画，效果和以前一样，但动画曲线复刻不出来了
 * PUSH -> 起始界面缩小到阈值，目标界面从阈值大小开始变大并透明度渐显直到完全覆盖
 * POP -> 方向反向，其余不变
 *
 * 参数：透明度，缩放
 * 预测式返回手势阈值：0.875f
 */
data class ScaleTransitionEffect(
    val reservedFgScale : Boolean? = false,
    val reservedBgScale : Boolean? = false,
    override val pageEffect : PageEffects = ScalePageEffects(reservedFgScale,reservedBgScale),
    override val predictiveMinValue: Float = 0.625f,
    override val pushAnimation: AnimationSpec<Float> = tween(400, easing = FastOutSlowInEasing),
    override val popAnimation: AnimationSpec<Float> = tween(400, easing = FastOutSlowInEasing)
) : TransitionEffect

fun ScalePageEffects(
    reservedFgScale : Boolean? = false,
    reservedBgScale : Boolean? = true,
) : PageEffects {
    return PageEffects(
        backgroundEffect = BackgroundPageEffectState(
            enableMirror = true,
            effect = PageEffect(
                corner = EffectValue.const(NoneRoundShape),
                alpha = EffectValue(
                    start = 1f,
                    end = 0f
                ),
                scale = EffectValue(
                    start = 1f,
                    end = when(reservedBgScale) {
                        true -> 2-NavigationController.DEFAULT_SHARED_MAX_PRECENT
                        false -> NavigationController.DEFAULT_SHARED_MAX_PRECENT
                        null -> 1f
                    }
                ),
                blur = EffectValue(
                    start = 0.dp,
                    end = 10.dp
                )
            )
        ),
        foregroundEffect = ForegroundPageEffectState(
            enableMirror = true,
            effect = PageEffect(
                corner = EffectValue.const(NoneRoundShape),
                alpha = EffectValue(
                    start = 0f,
                    end = 1f
                ),
                scale = EffectValue(
                    start = when(reservedFgScale) {
                        true -> 2-NavigationController.DEFAULT_SHARED_MAX_PRECENT
                        false -> NavigationController.DEFAULT_SHARED_MAX_PRECENT
                        null -> 1f
                    },
                    end = 1f
                ),
                blur = EffectValue(
                    start = 10.dp,
                    end = 0.dp
                )
            )
        )
    )
}

val DefaultLevelNoneTransitionEffect = ScaleTransitionEffect(
    pageEffect = ScalePageEffects().let { effects ->
        PageEffects(
            backgroundEffect = effects.backgroundEffect.copy(
                effect = effects.backgroundEffect.effect.copy(
                    blur = EffectValue.const(0.dp),
                    scale = EffectValue.const(1f)
                )
            ),
            foregroundEffect = effects.foregroundEffect.copy(
                effect = effects.foregroundEffect.effect.copy(
                    blur = EffectValue.const(0.dp),
                )
            ),
        )
    }
)
