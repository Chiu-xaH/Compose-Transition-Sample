package com.xah.navigation.anim

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.anim.PageEffect
import com.xah.navigation.model.anim.PageEffectState
import com.xah.navigation.model.anim.PageEffects

@Composable
fun rememberDefaultPageEffects(): PageEffects {
    val view = LocalView.current
    val corner = ScreenCornerHelper(view).getCornerDp()
    return remember(corner) {
        DefaultPageEffects(corner)
    }
}

fun DefaultPageEffects() = DefaultPageEffects(ScreenCornerHelper.corner)


private fun DefaultPageEffects(corner : Dp) : PageEffects {
    val foregroundOrigin = TransformOrigin(0.5f, 0.275f)
    return PageEffects(
        backgroundEffect = PageEffectState(
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
        foregroundEffect = PageEffectState(
            enableMirror = false,
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


/**
 * 前景带模糊版，性能警告
 */
@Composable
fun rememberDefaultPageEffectsEnhance(): PageEffects {
    val view = LocalView.current
    val corner = ScreenCornerHelper(view).getCornerDp()
    return remember(corner) {
        DefaultPageEffectsEnhance(corner)
    }
}

fun DefaultPageEffectsEnhance() = DefaultPageEffectsEnhance(ScreenCornerHelper.corner)

private fun DefaultPageEffectsEnhance(corner : Dp) : PageEffects {
    val foregroundOrigin = TransformOrigin(0.5f, 0.275f)
    return PageEffects(
        backgroundEffect = PageEffectState(
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
        foregroundEffect = PageEffectState(
            enableMirror = false,
            start = PageEffect(
                scale = 0f,
                blur = 20.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner * 2.25f),
                alpha = 1f,
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

/**
 * 灵动岛
 */
@Composable
fun rememberIslandPageEffects(): PageEffects {
    val view = LocalView.current
    val corner = ScreenCornerHelper(view).getCornerDp()
    return remember(corner) {
        IslandPageEffects(corner)
    }
}

fun IslandPageEffects() = IslandPageEffects(ScreenCornerHelper.corner)

private fun IslandPageEffects(corner : Dp) : PageEffects {
    return PageEffects(
        backgroundEffect = PageEffectState(
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
        foregroundEffect = PageEffectState(
            enableMirror = false,
            start = PageEffect(
                scale = 0f,
                blur = 12.5.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner * 2.25f),
                alpha = 1f,
                position = TransformOrigin(0.5f, 0f)
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

@Composable
fun rememberSlideFromBottomPageEffects(): PageEffects {
    val view = LocalView.current
    val corner = ScreenCornerHelper(view).getCornerDp()
    return remember(corner) {
        SlideFromBottomPageEffects(corner)
    }
}

fun SlideFromBottomPageEffects() = SlideFromBottomPageEffects(ScreenCornerHelper.corner)

private fun SlideFromBottomPageEffects(corner : Dp) : PageEffects {
    return PageEffects(
        backgroundEffect = PageEffectState(
            enableMirror = true,
            start = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(0.dp),
                alpha = 1f,
            ),
            end = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0.25f,
                corner = RoundedCornerShape(0.dp),
                alpha = 1f,
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
                translationPercent = Offset(0f,1f),
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

@Composable
fun rememberSlideFromEndPageEffects(): PageEffects {
    val view = LocalView.current
    val corner = ScreenCornerHelper(view).getCornerDp()
    return remember(corner) {
        SlideFromEndPageEffects(corner)
    }
}

fun SlideFromEndPageEffects() = SlideFromEndPageEffects(ScreenCornerHelper.corner)

private fun SlideFromEndPageEffects(corner : Dp) : PageEffects {
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
    return PageEffects(
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
    )
}

