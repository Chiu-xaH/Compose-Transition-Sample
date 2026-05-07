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
    return DefaultPageEffects(corner).copy(
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
    return DefaultPageEffects(corner).copy(
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
                blur = 15.dp,
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
                translationPercent = from,
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
fun rememberFlipPageEffects(): PageEffects {
    val view = LocalView.current
    val corner = ScreenCornerHelper(view).getCornerDp()
    return remember(corner) {
        FlipPageEffects(corner)
    }
}

fun FlipPageEffects() = FlipPageEffects(ScreenCornerHelper.corner)

private fun FlipPageEffects(corner : Dp) : PageEffects {
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

