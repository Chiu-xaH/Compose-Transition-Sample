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
import com.sharednav.common.helper.AnimationSpecManager
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.anim.effect.EffectValue
import com.xah.navigation.model.anim.effect.ForegroundPageEffectState
import com.xah.navigation.model.anim.effect.PageEffect
import com.xah.navigation.model.anim.effect.PageEffects
import com.xah.navigation.model.anim.TransitionEffect
import com.xah.navigation.model.anim.effect.sub.Rotation

/**
 * 灵动岛
 * eg: 灵动岛、肩键
 * PUSH -> 起始界面模糊、压暗、缩放，目标界面从position飞到中央并放大，直到完全覆盖
 * POP -> 方向反向，其余不变
 *
 * 参数：若干
 * 预测式返回手势阈值：0.875f
 */
data class IslandTransitionEffect(
    val position: TransformOrigin = TransformOrigin(0.5f, 0f),
    val rotation: Rotation = Rotation(),
    override val pageEffect : PageEffects = IslandPageEffects(position,rotation),
    override val predictiveMinValue: Float = NavigationController.DEFAULT_SHARED_MAX_PRECENT,
    override val pushAnimation: AnimationSpec<Float> = tween(AnimationSpecManager.DEFAULT_SHARED_SPEC*6/5, easing = NavigationController.DEFAULT_EASING),
    override val popAnimation: AnimationSpec<Float> = tween(AnimationSpecManager.DEFAULT_SHARED_SPEC*6/5, easing = NavigationController.DEFAULT_EASING)
) : TransitionEffect


@Composable
fun rememberIslandPageEffects(position: TransformOrigin = TransformOrigin(0.5f, 0f),rotation: Rotation = Rotation()): PageEffects {
    val view = LocalView.current
    val corner = ScreenCornerHelper(view).getCornerDp()
    return remember(corner) {
        IslandPageEffects(corner,position,rotation)
    }
}

fun IslandPageEffects(position: TransformOrigin = TransformOrigin(0.5f, 0f),rotation: Rotation = Rotation()) = IslandPageEffects(ScreenCornerHelper.corner,position,rotation)

private fun IslandPageEffects(corner : Dp,position : TransformOrigin,rotation: Rotation) : PageEffects {
    return PageEffects(
        backgroundEffect = DefaultPageEffects(corner).backgroundEffect,
        foregroundEffect = ForegroundPageEffectState(
            effect = PageEffect(
                scale = EffectValue(
                    start = 0f,
                    end = 1f
                ),
                blur = EffectValue(
                    start = 15.dp,
                    end = 0.dp
                ),
                corner = EffectValue(
                    start = RoundedCornerShape(corner * 2.25f),
                    end = RoundedCornerShape(corner)
                ),
                position = EffectValue(
                    start = position,
                    end = TransformOrigin(0.5f, 0.5f)
                ),
                rotate = EffectValue(
                    start = rotation,
                    end = Rotation()
                )
            )
        )
    )
}
