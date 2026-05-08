package com.xah.navigation.anim.effect

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.anim.BackgroundPageEffectState
import com.xah.navigation.model.anim.ForegroundPageEffectState
import com.xah.navigation.model.anim.PageEffect
import com.xah.navigation.model.anim.PageEffects
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
    override val pushAnimation: AnimationSpec<Float> = tween(NavigationController.DEFAULT_SHARED_SPEC*6/5, easing = NavigationController.DEFAULT_EASING),
    override val popAnimation: AnimationSpec<Float> = tween(NavigationController.DEFAULT_SHARED_SPEC*6/5, easing = NavigationController.DEFAULT_EASING)
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
    val foregroundOrigin = TransformOrigin(0.5f, 0.275f)
    return PageEffects(
        backgroundEffect = BackgroundPageEffectState(
            enableMirror = true,
            start = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(0.dp),
                alpha = 1f,
            ),
            end = PageEffect(
                scale = NavigationController.DEFAULT_SHARED_MAX_PRECENT,
                blur = 25.dp,
                mask = 0.25f,
                corner = RoundedCornerShape(0.dp),
                alpha = 1f,
            )
        ),
        foregroundEffect = ForegroundPageEffectState(
            start = PageEffect(
                scale = 0f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner * 2.25f),
                alpha = 0.75f,
                position = foregroundOrigin
            ),
            end = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner),
                alpha = 1f,
                position = foregroundOrigin
            )
        )
    )
}
