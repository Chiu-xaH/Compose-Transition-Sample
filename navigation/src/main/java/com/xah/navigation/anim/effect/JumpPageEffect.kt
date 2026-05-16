package com.xah.navigation.anim.effect

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.anim.BackgroundPageEffectState
import com.xah.navigation.model.anim.EffectLevel
import com.xah.navigation.model.anim.EffectValue
import com.xah.navigation.model.anim.ForegroundPageEffectState
import com.xah.navigation.model.anim.PageEffect
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
    override val predictiveMinValue: Float = (0.75f+0.8f)/2f,
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
    val maxFlyValue = 1f
    val maxScaleValue = 0.9f
    return object : PageEffects(
        backgroundEffect = BackgroundPageEffectState(
            enableMirror = false,
            backgroundColor = Color.Black,
            effect = PageEffect(
                scale = EffectValue(
                    start = 1f,
                    end = maxScaleValue,
                    reserved = true
                ),
                alpha = EffectValue(
                    start = 1f,
                    end = 0f
                ),
                corner = EffectValue.const(RoundedCornerShape(corner)),
                translationPercent = EffectValue(
                    start = Offset.Zero,
                    end = Offset(-maxFlyValue,0f)
                )
            )
        ),
        foregroundEffect = ForegroundPageEffectState(
            effect = PageEffect(
                scale = EffectValue(
                    start = 1f,
                    end = maxScaleValue,
                    reserved = true
                ),
                alpha = EffectValue(
                    start = 0f,
                    end = 1f
                ),
                corner = EffectValue.const(RoundedCornerShape(corner)),
                translationPercent = EffectValue(
                    start = Offset(maxFlyValue,0f),
                    end = Offset.Zero,
                )
            )
        )
    ) {
        // 重写方法，此PageEffect效果不受EffectLevel影响
        override fun background(progress: Float, level: EffectLevel) = backgroundEffect.lerp(progress)

        override fun foreground(progress: Float, level: EffectLevel) = foregroundEffect.lerp(progress)
    }
}

