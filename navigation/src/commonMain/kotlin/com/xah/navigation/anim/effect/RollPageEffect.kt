package com.xah.navigation.anim.effect

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.Dp
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.navigation.model.anim.effect.sub.Roll

/**
 * 从四周揭示（Reveal）效果
 * eg: 底部弹出面板、底部弹窗
 * PUSH -> 起始界面压暗，目标界面从对应方向逐步揭示直到完全覆盖
 *         与 Slide 不同，内容不移动，只有边界移动（类似舞台幕布拉开）
 * POP -> 方向反向，其余不变
 *
 * 参数：压暗程度、是否圆角裁剪
 * 预测式返回手势阈值：0.875f
 */
data class RollTransitionEffect(
    val direction: Direction = Direction.TOP,
    val clip : Boolean = true,
    override val pageEffect : PageEffects = RollPageEffects(direction,clip),
    override val predictiveMinValue: Float = NavigationController.DEFAULT_SHARED_MAX_PRECENT,
    override val pushAnimation: AnimationSpec<Float> = tween(400, easing = NavigationController.DEFAULT_EASING),
    override val popAnimation: AnimationSpec<Float> = tween(400, easing = NavigationController.DEFAULT_EASING)
) : TransitionEffect

@Composable
expect fun rememberRevealPageEffects(
    direction : Direction = Direction.TOP,
    clip : Boolean = true
): PageEffects

fun RollPageEffects(
    direction : Direction = Direction.TOP,
    clip : Boolean = true
) = RollPageEffects(ScreenCornerHelper.corner, direction, clip)

fun RollPageEffects(
    corner : Dp,
    direction : Direction,
    clip : Boolean
) : PageEffects {
    val clipRevealStart = when(direction) {
        Direction.TOP -> Roll(bottom = 1f)
        Direction.BOTTOM -> Roll(top = 1f)
        Direction.START -> Roll(right = 1f)
        Direction.END -> Roll(left = 1f)
    }

    return PageEffects(
        backgroundEffect = BackgroundPageEffectState(
            enableMirror = true,
            effect = PageEffect(
                mask = EffectValue(
                    start = noneMask,
                    end = defaultMask
                ),
                corner = EffectValue.const(NoneRoundShape),
            )
        ),
        foregroundEffect = ForegroundPageEffectState(
            effect = PageEffect(
                corner = EffectValue.const(
                    if(clip)
                        androidx.compose.foundation.shape.RoundedCornerShape(corner)
                    else
                        NoneRoundShape
                ),
                roll = EffectValue(
                    start = clipRevealStart,
                    end = Roll.None
                ),
                // 微位移：内容随方向移动 1/3，reveal 裁剪边界移动剩余 2/3
                translationPercent = EffectValue(
                    start = when(direction) {
                        Direction.TOP -> Offset(0f, -1/3f)
                        Direction.BOTTOM -> Offset(0f, 1/3f)
                        Direction.START -> Offset(-1/3f, 0f)
                        Direction.END -> Offset(1/3f, 0f)
                    },
                    end = Offset.Zero
                )
            )
        )
    )
}
