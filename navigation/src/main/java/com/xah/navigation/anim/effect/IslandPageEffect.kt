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
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.anim.PageEffect
import com.xah.navigation.model.anim.PageEffectState
import com.xah.navigation.model.anim.PageEffects
import com.xah.navigation.model.anim.TransitionEffect

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
    override val pageEffect : PageEffects = IslandPageEffects(position),
    override val predictiveMinValue: Float = NavigationController.DEFAULT_SHARED_MAX_PRECENT,
    override val pushAnimation: AnimationSpec<Float> = tween(NavigationController.DEFAULT_SHARED_SPEC*6/5, easing = NavigationController.DEFAULT_EASING),
    override val popAnimation: AnimationSpec<Float> = tween(NavigationController.DEFAULT_SHARED_SPEC*6/5, easing = NavigationController.DEFAULT_EASING)
) : TransitionEffect


@Composable
fun rememberIslandPageEffects(position: TransformOrigin = TransformOrigin(0.5f, 0f)): PageEffects {
    val view = LocalView.current
    val corner = ScreenCornerHelper(view).getCornerDp()
    return remember(corner) {
        IslandPageEffects(corner,position)
    }
}

fun IslandPageEffects(position: TransformOrigin = TransformOrigin(0.5f, 0f)) = IslandPageEffects(ScreenCornerHelper.corner,position)

private fun IslandPageEffects(corner : Dp,position : TransformOrigin) : PageEffects {
    return PageEffects(
        backgroundEffect = DefaultPageEffects(corner).backgroundEffect,
        foregroundEffect = PageEffectState(
            enableMirror = false,
            start = PageEffect(
                scale = 0f,
                blur = 15.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner * 2.25f),
                alpha = 1f,
                position = position
            ),
            end = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner),
                alpha = 1f,
                position = TransformOrigin(0.5f, 0.5f)
            )
        )
    )
}
