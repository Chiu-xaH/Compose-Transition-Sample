package com.xah.navigation.anim.effect

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import com.sharednav.common.helper.NoneRoundShape
import com.xah.navigation.model.anim.TransitionEffect
import com.xah.navigation.model.anim.effect.BackgroundPageEffectState
import com.xah.navigation.model.anim.effect.EffectValue
import com.xah.navigation.model.anim.effect.ForegroundPageEffectState
import com.xah.navigation.model.anim.effect.PageEffect
import com.xah.navigation.model.anim.effect.PageEffects

/**
 * 无动画
 * eg: 没有eg，一般可用于不需要动画的场景
 * PUSH -> 起始与目标界面逐瞬间切换
 * POP -> 起始与目标界面逐瞬间切换
 *
 * 预测式返回手势阈值：0.5f
 */
data class NoneTransitionEffect(
    override val pageEffect : PageEffects = NonePageEffects(),
    override val predictiveMinValue: Float = 1f,
    override val pushAnimation: AnimationSpec<Float> = tween(0),
    override val popAnimation: AnimationSpec<Float> = tween(0)
) : TransitionEffect

fun NonePageEffects() : PageEffects {
    return PageEffects(
        backgroundEffect = BackgroundPageEffectState(
            enableMirror = true,
            effect = PageEffect(
                corner = EffectValue.const(NoneRoundShape),
            )
        ),
        foregroundEffect = ForegroundPageEffectState(
            enableMirror = true,
            effect = PageEffect(
                corner = EffectValue.const(NoneRoundShape)
            )
        )
    )
}
