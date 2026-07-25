package com.xah.navigation.anim.effect

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.ui.graphics.Color
import com.sharednav.common.helper.NoneRoundShape
import com.sharednav.common.modifier.noneMask
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.anim.TransitionEffect
import com.xah.navigation.model.anim.effect.BackgroundPageEffectState
import com.xah.navigation.model.anim.effect.EffectValue
import com.xah.navigation.model.anim.effect.ForegroundPageEffectState
import com.xah.navigation.model.anim.effect.PageEffect
import com.xah.navigation.model.anim.effect.PageEffects

/**
 * 默认浮窗动画
 * eg：iOS弹窗
 * 起始界面压暗，目标界面从从略大缩小到1f并带透明度变化
 *
 * 参数：若干
 * 预测式返回手势阈值：0.875f
 */
data class WindowTransitionEffect(
    override val pageEffect : PageEffects = WindowPageEffects(),
    override val predictiveMinValue: Float = NavigationController.DEFAULT_SHARED_MAX_PRECENT,
    override val pushAnimation: AnimationSpec<Float> = tween(400),
    override val popAnimation: AnimationSpec<Float> = tween(400)
) : TransitionEffect

fun WindowPageEffects(
    corner: EffectValue<CornerBasedShape> = EffectValue.const(NoneRoundShape)
) : PageEffects {
    return PageEffects(
        backgroundEffect = BackgroundPageEffectState(
            enableMirror = true,
            effect = PageEffect(
                maskLight = EffectValue(
                    start = noneMask,
                    end = Color.Black.copy(0.3f)
                ),
                corner = EffectValue.const(NoneRoundShape),
            )
        ),
        foregroundEffect = ForegroundPageEffectState(
            PageEffect(
                scale = EffectValue(
                    start = 1.1f,
                    end = 1f,
                ),
                alpha = EffectValue(
                    start = 0f,
                    end = 1f
                ),
                corner = corner,
            )
        )
    )
}
