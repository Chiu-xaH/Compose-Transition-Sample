package com.xah.navigation.anim.effect

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sharednav.common.helper.ScreenCornerHelper
import com.sharednav.common.modifier.defaultMask
import com.sharednav.common.modifier.noneMask
import com.sharednav.common.util.NoneRoundShape
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.anim.effect.BackgroundPageEffectState
import com.xah.navigation.model.anim.effect.EffectValue
import com.xah.navigation.model.anim.effect.ForegroundPageEffectState
import com.xah.navigation.model.anim.effect.PageEffect
import com.xah.navigation.model.anim.effect.PageEffects
import com.xah.navigation.model.anim.TransitionEffect

/**
 * 缩放
 * eg: 桌面动效
 * 起始界面缩小到一定阈值0.875f并模糊、缩放、压暗，目标界面从屏幕中央偏上位置开始缩放变大直到完全覆盖
 *
 * 参数：若干
 * 预测式返回手势阈值：0.875f
 */
data class DefaultTransitionEffect(
    override val pageEffect : PageEffects = DefaultPageEffects(),
    override val predictiveMinValue: Float = NavigationController.DEFAULT_SHARED_MAX_PRECENT,
    override val pushAnimation: AnimationSpec<Float> = tween(NavigationController.DEFAULT_ANIMATION_TIME*6/5, easing = NavigationController.DEFAULT_EASING),
    override val popAnimation: AnimationSpec<Float> = tween(NavigationController.DEFAULT_ANIMATION_TIME*6/5, easing = NavigationController.DEFAULT_EASING)
) : TransitionEffect

@Composable
fun rememberDefaultPageEffects(): PageEffects {
    val view = LocalView.current
    val corner = ScreenCornerHelper(view).getCornerDp()
    return remember(corner) {
        DefaultPageEffects(corner)
    }
}

fun DefaultPageEffects() = DefaultPageEffects(ScreenCornerHelper.corner)

fun DefaultPageEffects(corner : Dp) : PageEffects {
    return PageEffects(
        backgroundEffect = BackgroundPageEffectState(
            enableMirror = true,
            effect = PageEffect(
                scale = EffectValue(
                    start = 1f,
                    end = NavigationController.DEFAULT_SHARED_MAX_PRECENT,
                ),
                blur = EffectValue(
                    start = 0.dp,
                    end = 25.dp
                ),
                mask = EffectValue(
                    start = noneMask,
                    end = defaultMask
                ),
                corner = EffectValue.const(NoneRoundShape),
            )
        ),
        foregroundEffect = ForegroundPageEffectState(
            PageEffect(
                scale = EffectValue(
                    start = 0f,
                    end = 1f,
                ),
                corner = EffectValue(
                    start = RoundedCornerShape(corner * 2.25f),
                    end = RoundedCornerShape(corner)
                ),
                alpha = EffectValue(
                    start = 0.75f,
                    end = 1f
                ),
                position = EffectValue.const(TransformOrigin(0.5f, 0.275f))
            )
        )
    )
}
