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
import com.xah.navigation.model.anim.EffectLevel
import com.xah.navigation.model.anim.PageEffect
import com.xah.navigation.model.anim.PageEffectState
import com.xah.navigation.model.anim.PageEffects
import com.xah.navigation.model.anim.TransitionEffect

/**
 * 跳转
 * eg: 手机系统中的应用跳转
 * PUSH -> 起始界面缩小并位移向左，目标界面从右侧放大并位移向右，两者缩放统一，背景为纯黑色
 * POP -> 方向反向，其余不变
 *
 * 参数：缩放程度、位移程度
 * 预测式返回手势阈值：0.5f
 */
data class JumpTransitionEffect(
    override val pageEffect : PageEffects = JumpPageEffects(),
    override val predictiveMinValue: Float = 0.5f,
    override val pushAnimation: AnimationSpec<Float> = tween(450, easing = NavigationController.DEFAULT_EASING),
    override val popAnimation: AnimationSpec<Float> = tween(450, easing = NavigationController.DEFAULT_EASING)
) : TransitionEffect

@Composable
fun rememberJumpPageEffects(): PageEffects {
    val view = LocalView.current
    val corner = ScreenCornerHelper(view).getCornerDp()
    return remember(corner) {
        JumpPageEffects(corner)
    }
}

fun JumpPageEffects() = JumpPageEffects(ScreenCornerHelper.corner)

private fun JumpPageEffects(corner : Dp) : PageEffects {
    return object : PageEffects(
        backgroundEffect = PageEffectState(
            enableMirror = false,
            start = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner),
                alpha = 1f,
                translationPercent = Offset(0f,0f),
            ),
            end = PageEffect(
                scale = 0.85f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner),
                alpha = 1f,
                translationPercent = Offset(-1f,0f),
            )
        ),
        foregroundEffect = PageEffectState(
            enableMirror = false,
            start = PageEffect(
                scale = 0.85f,
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
    ) {
        override fun background(progress: Float, level: EffectLevel) = backgroundEffect.lerp(progress)

        override fun foreground(progress: Float, level: EffectLevel) = foregroundEffect.lerp(progress)
    }
}

