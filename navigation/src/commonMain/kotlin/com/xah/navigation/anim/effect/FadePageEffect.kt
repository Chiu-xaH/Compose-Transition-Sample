package com.xah.navigation.anim.effect

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import com.sharednav.common.helper.NoneRoundShape
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.anim.TransitionEffect
import com.xah.navigation.model.anim.effect.BackgroundPageEffectState
import com.xah.navigation.model.anim.effect.EffectValue
import com.xah.navigation.model.anim.effect.ForegroundPageEffectState
import com.xah.navigation.model.anim.effect.PageEffect
import com.xah.navigation.model.anim.effect.PageEffects


/**
 * 渐变盖住
 * eg: 没有eg，一般可用于两个界面之间布局相似度很高的情况，例如骨架屏？
 * PUSH -> 起始界面不变，目标界面逐渐显示直到完全覆盖
 * POP -> 方向反向，其余不变
 *
 * 参数：透明度
 * 预测式返回手势阈值：0.5f
 */
data class FadeTransitionEffect(
    override val pageEffect : PageEffects = FadePageEffects(),
    override val predictiveMinValue: Float = 0.75f,
    override val pushAnimation: AnimationSpec<Float> = tween(400, easing = NavigationController.DEFAULT_EASING),
    override val popAnimation: AnimationSpec<Float> = tween(400, easing = NavigationController.DEFAULT_EASING)
) : TransitionEffect

fun FadePageEffects() : PageEffects {
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
                corner = EffectValue.const(NoneRoundShape),
                alpha = EffectValue(
                    start = 0f,
                    end = 1f
                )
            )
        )
    )
}
