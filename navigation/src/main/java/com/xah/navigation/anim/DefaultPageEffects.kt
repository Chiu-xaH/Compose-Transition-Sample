package com.xah.navigation.anim

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.navigation.anim.PageEffect
import com.xah.navigation.anim.PageEffectState
import com.xah.navigation.anim.PageEffects


@Composable
fun rememberDefaultPageEffects(): PageEffects {
    val corner = ScreenCornerHelper.corner
    return remember(corner) {
        DefaultPageEffects(corner)
    }
}

private fun DefaultPageEffects(corner : Dp) : PageEffects {
    return PageEffects(
        backgroundEffect = PageEffectState(
            start = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(0.dp),
                alpha = 1f,
            ),
            end = PageEffect(
                scale = 0.875f,
                blur = 25.dp,
                mask = 0.25f,
                corner = RoundedCornerShape(0.dp),
                alpha = 1f
            )
        ),
        foregroundEffect = PageEffectState(
            start = PageEffect(
                scale = 0f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner * 2.25f),
                alpha = 0.75f
            ),
            end = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner),
                alpha = 1f,
            )
        ),
        foregroundOrigin = TransformOrigin(0.5f, 0.275f)
    )
}


/**
 * 前景带模糊版，性能警告
 */
@Composable
fun rememberDefaultPageEffectsEnhance(): PageEffects {
    val corner = ScreenCornerHelper.corner
    return remember(corner) {
        DefaultPageEffectsEnhance(corner)
    }
}

private fun DefaultPageEffectsEnhance(corner : Dp) : PageEffects {
    return PageEffects(
        backgroundEffect = PageEffectState(
            start = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(0.dp),
                alpha = 1f,
            ),
            end = PageEffect(
                scale = 0.875f,
                blur = 25.dp,
                mask = 0.25f,
                corner = RoundedCornerShape(0.dp),
                alpha = 1f
            )
        ),
        foregroundEffect = PageEffectState(
            start = PageEffect(
                scale = 0f,
                blur = 20.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner * 2.25f),
                alpha = 1f
            ),
            end = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner),
                alpha = 1f,
            )
        ),
        foregroundOrigin = TransformOrigin(0.5f, 0.275f)
    )
}