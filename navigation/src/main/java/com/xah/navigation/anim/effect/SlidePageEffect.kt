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
 * 从四周滑入
 * eg: 微信支付完成弹窗
 * PUSH -> 起始界面压暗，目标界面从底部推入直到完全覆盖
 * POP -> 方向反向，其余不变
 *
 * 参数：压暗程度
 * 预测式返回手势阈值：0.875f
 */
data class SlideTransitionEffect(
    val direction: Direction = Direction.BOTTOM,
    override val pageEffect : PageEffects = SlidePageEffects(direction),
    override val predictiveMinValue: Float = NavigationController.DEFAULT_SHARED_MAX_PRECENT,
    override val pushAnimation: AnimationSpec<Float> = tween(400, easing = NavigationController.DEFAULT_EASING),
    override val popAnimation: AnimationSpec<Float> = tween(400, easing = NavigationController.DEFAULT_EASING)
) : TransitionEffect

@Composable
fun rememberSlidePageEffects(direction : Direction): PageEffects {
    val view = LocalView.current
    val corner = ScreenCornerHelper(view).getCornerDp()
    return remember(corner) {
        SlidePageEffects(corner,direction)
    }
}

fun SlidePageEffects(direction : Direction) = SlidePageEffects(ScreenCornerHelper.corner,direction)

enum class Direction {
    TOP,
    BOTTOM,
    START,
    END
}

private fun SlidePageEffects(corner : Dp,direction : Direction) : PageEffects {
    val from = when(direction) {
        Direction.TOP -> Offset(0f,-1f)
        Direction.BOTTOM -> Offset(0f,1f)
        Direction.START -> Offset(-1f,0f)
        Direction.END -> Offset(1f,0f)
    }
    return PageEffects(
        backgroundEffect = BackgroundPageEffectState(
            enableMirror = true,
            effect = PageEffect(
                scale = EffectValue(
                    start = 1f,
                    end = NavigationController.DEFAULT_SHARED_MAX_PRECENT
                ),
                blur = EffectValue(
                    start = 0.dp,
                    end = 15.dp
                ),
                mask = EffectValue(
                    start = noneMask,
                    end = defaultMask
                ),
                corner = EffectValue.const(NoneRoundShape),
            )
        ),
        foregroundEffect = ForegroundPageEffectState(
            effect = PageEffect(
                corner = EffectValue.const(RoundedCornerShape(corner)),
                translationPercent = EffectValue(
                    start = from,
                    end = Offset.Zero
                )
            )
        )
    )
}
