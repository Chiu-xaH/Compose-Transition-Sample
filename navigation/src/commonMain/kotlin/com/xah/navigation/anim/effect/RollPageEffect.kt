package com.xah.navigation.anim.effect

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.navigation.model.anim.EffectLevel
import com.xah.navigation.model.anim.effect.PageEffectFrame
import com.xah.navigation.model.anim.effect.sub.Roll

/**
 * 幕布效果
 * eg: iOS下拉通知中心
 * PUSH -> 起始界面压暗，目标界面从对应方向逐步揭示直到完全覆盖
 *         与 Slide 不同，内容不移动，只有边界移动（类似舞台幕布拉开）
 * POP -> 方向反向，其余不变
 *
 * 参数：压暗程度、是否圆角裁剪
 * 预测式返回手势阈值：0.875f
 * 可以用Haze或BackDrop捕获背后画面，实现液态玻璃或模糊
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
expect fun rememberRollPageEffects(
    direction : Direction = Direction.TOP,
    clip : Boolean = true,
): PageEffects

fun RollPageEffects(
    direction : Direction = Direction.TOP,
    clip : Boolean = true,
) = RollPageEffects(ScreenCornerHelper.corner, direction, clip)

fun RollPageEffects(
    corner : Dp,
    direction : Direction,
    clip : Boolean,
) : PageEffects {
    val clipRevealStart = when(direction) {
        Direction.TOP -> Roll(bottom = 1f)
        Direction.BOTTOM -> Roll(top = 1f)
        Direction.START -> Roll(right = 1f)
        Direction.END -> Roll(left = 1f)
    }

    return object : PageEffects(
        backgroundEffect = BackgroundPageEffectState(
            enableMirror = true,
            effect = PageEffect(
                maskLight = EffectValue(
                    start = noneMask,
                    end = defaultMask,
                ),
                corner = EffectValue.const(NoneRoundShape),
            )
        ),
        foregroundEffect = ForegroundPageEffectState(
            effect = PageEffect(
                corner = EffectValue.const(
                    if(clip) {
                        RoundedCornerShape(corner)
                    } else {
                        NoneRoundShape
                    }
                ),
                roll = EffectValue(
                    start = clipRevealStart,
                    end = Roll.None
                ),
                innerBlur = EffectValue(
                    start = 25.dp,
                    end = 0.dp
                )
            )
        )
    ) {
        override fun background(progress: Float, level: EffectLevel): PageEffectFrame {
            return super.background(progress, level).let {
                if(progress != 1f) {
                    it
                } else {
                    it.copy(maskLight = Color.Transparent, maskDark = Color.Transparent)
                }
            }
        }
    }
}
