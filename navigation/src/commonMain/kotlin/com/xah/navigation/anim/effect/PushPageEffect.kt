package com.xah.navigation.anim.effect

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import com.sharednav.common.helper.ScreenCornerHelper
import com.sharednav.common.modifier.defaultMask
import com.sharednav.common.modifier.noneMask
import com.sharednav.common.helper.NoneRoundShape
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.anim.TransitionEffect
import com.xah.navigation.model.anim.effect.BackgroundPageEffectState
import com.xah.navigation.model.anim.effect.EffectValue
import com.xah.navigation.model.anim.effect.ForegroundPageEffectState
import com.xah.navigation.model.anim.effect.PageEffect
import com.xah.navigation.model.anim.effect.PageEffects

/**
 * 从右侧滑入
 * eg: 设置的多级界面
 * PUSH -> 起始界面位移向左1/3并压暗，目标界面从右侧推入直到完全覆盖
 * POP -> 方向反向，其余不变
 *
 * 参数：位移程度、压暗程度
 * 预测式返回手势阈值：0.8f
 */
data class PushTransitionEffect(
    val clip : Boolean = true,
    override val pageEffect : PageEffects = PushPageEffects(clip),
    override val predictiveMinValue: Float = 0.8f,
    override val pushAnimation: AnimationSpec<Float> = tween(450, easing = NavigationController.DEFAULT_EASING),
    override val popAnimation: AnimationSpec<Float> = tween(450, easing = NavigationController.DEFAULT_EASING)
) : TransitionEffect

@Composable
expect fun rememberPushPageEffects(clip : Boolean = true): PageEffects

fun PushPageEffects(clip : Boolean = true) = PushPageEffects(ScreenCornerHelper.corner,clip)

fun PushPageEffects(corner : Dp, clip : Boolean) : PageEffects {
    return PageEffects(
        backgroundEffect = BackgroundPageEffectState(
            enableMirror = true,
            effect = PageEffect(
                maskLight = EffectValue(
                    start = noneMask,
                    end = defaultMask
                ),
                corner = EffectValue.const(NoneRoundShape),
                translationPercent = EffectValue(
                    start = Offset.Zero,
                    end = Offset(-1/3f,0f)
                )
            ),
        ),
        foregroundEffect = ForegroundPageEffectState(
            effect = PageEffect(
                corner = EffectValue.const(
                    if(clip)
                        RoundedCornerShape(corner)
                    else
                        NoneRoundShape
                ),
                translationPercent = EffectValue(
                    start = Offset(1f,0f),
                    end = Offset.Zero
                )
            )
        )
    )
}
