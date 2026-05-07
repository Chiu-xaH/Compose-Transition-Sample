package com.xah.navigation.anim.effect

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.anim.PageEffect
import com.xah.navigation.model.anim.PageEffectState
import com.xah.navigation.model.anim.PageEffects
import com.xah.navigation.model.anim.TransitionEffect

/**
 * 从右侧滑入
 * eg: 设置的多级界面
 * PUSH -> 起始界面位移向左1/3并压暗，目标界面从右侧推入直到完全覆盖
 * POP -> 方向反向，其余不变
 *
 * 参数：位移程度、压暗程度
 * 预测式返回手势阈值：0.8f
 */
data class FlipTransitionEffect(
    override val pageEffect : PageEffects = FlipPageEffects(),
    override val predictiveMinValue: Float = 0.8f,
    override val pushAnimation: AnimationSpec<Float> = tween(450, easing = NavigationController.DEFAULT_EASING),
    override val popAnimation: AnimationSpec<Float> = tween(450, easing = NavigationController.DEFAULT_EASING)
) : TransitionEffect

@Composable
fun rememberFlipPageEffects(): PageEffects {
    val view = LocalView.current
    val corner = ScreenCornerHelper(view).getCornerDp()
    return remember(corner) {
        FlipPageEffects(corner)
    }
}

fun FlipPageEffects() = FlipPageEffects(ScreenCornerHelper.corner)

private fun FlipPageEffects(corner : Dp) : PageEffects {
    return PageEffects(
        backgroundEffect = PageEffectState(
            enableMirror = true,
            start = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(0.dp),
                alpha = 1f,
                translationPercent = Offset(0f,0f),
            ),
            end = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0.25f,
                corner = RoundedCornerShape(0.dp),
                alpha = 1f,
                translationPercent = Offset(-1/3f,0f),
            )
        ),
        foregroundEffect = PageEffectState(
            enableMirror = false,
            start = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner),
                alpha = 1f,
                translationPercent = Offset(1f,0f),
            ),
            end = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner),
                alpha = 1f,
                translationPercent = Offset(0f,0f),
            )
        )
    )
}
