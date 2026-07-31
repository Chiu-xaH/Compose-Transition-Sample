package com.xah.navigation.anim.effect

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.sharednav.common.helper.ScreenCornerHelper
import com.sharednav.common.modifier.defaultMask
import com.sharednav.common.modifier.noneMask
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.anim.TransitionEffect
import com.xah.navigation.model.anim.effect.BackgroundPageEffectState
import com.xah.navigation.model.anim.effect.EffectValue
import com.xah.navigation.model.anim.effect.ForegroundPageEffectState
import com.xah.navigation.model.anim.effect.PageEffect
import com.xah.navigation.model.anim.effect.PageEffects
import com.xah.navigation.model.anim.effect.sub.BgEffectBackground

private val SLIDE_EASING = CubicBezierEasing(0.5f, .25f, 0.1f, 1.0f)
/**
 * 从四周滑入2 仿iOS
 * eg: 微信支付完成弹窗
 * PUSH -> 起始界面压暗，目标界面从底部推入直到完全覆盖
 * POP -> 方向反向，其余不变
 *
 * 参数：压暗程度
 * 预测式返回手势阈值：0.875f
 */
data class SlideTransitionEffect2(
    val direction: Direction = Direction.BOTTOM,
    override val pageEffect : PageEffects = SlidePageEffects2(direction),
    override val predictiveMinValue: Float = 0.9f,
    override val pushAnimation: AnimationSpec<Float> = tween(400, easing = SLIDE_EASING),
    override val popAnimation: AnimationSpec<Float> = tween(400, easing = SLIDE_EASING)
) : TransitionEffect

@Composable
expect fun rememberSlidePageEffects2(
    direction : Direction = Direction.BOTTOM,
): PageEffects

fun SlidePageEffects2(
    direction : Direction = Direction.BOTTOM,
) = SlidePageEffects2(ScreenCornerHelper.corner,direction)

fun SlidePageEffects2(corner : Dp, direction : Direction) : PageEffects {
    val from = when(direction) {
        Direction.TOP -> Offset(0f,-1f)
        Direction.BOTTOM -> Offset(0f,1f)
        Direction.START -> Offset(-1f,0f)
        Direction.END -> Offset(1f,0f)
    }
    return PageEffects(
        backgroundEffect = BackgroundPageEffectState(
            enableMirror = false,
            backgroundColor = BgEffectBackground.Color(Color.Black),
            effect = PageEffect(
                scale = EffectValue(
                    start = 1f,
                    end = 0.9f
                ),
                alpha = EffectValue(
                    start = 1f,
                    end = 0.5f
                ),
                corner = EffectValue.const(RoundedCornerShape(corner)),
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
