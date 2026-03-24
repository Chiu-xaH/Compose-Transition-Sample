package com.xah.floating.anim

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp

@Immutable
data class PageEffect(
    val scale: Float,
    val blur: Dp,
    val mask: Float,
) {
    fun lerp(progress : Float): PageEffect {
        val end = NonePageEffect
        return PageEffect(
            scale = lerp(scale, end.scale, progress),
            blur = lerp(blur, end.blur, progress),
            mask = lerp(mask, end.mask, progress),
        )
    }

    companion object {
        val DefaultPageEffect = PageEffect(
            scale = 1f,
            blur = 0.dp,
            mask = 0.3f
        )
        val NonePageEffect = PageEffect(
            scale = 1f,
            blur = 0.dp,
            mask = 0f
        )
    }
}

@Immutable
data class BackgroundEffect(
    val pageEffect : PageEffect = PageEffect.DefaultPageEffect,
    val animationSpec: AnimationSpec<Float> = spring()
)

@Immutable
data class ForegroundEffect(
    val enter : EnterTransition = scaleIn(initialScale = 1.1f),
    val exit : ExitTransition = scaleOut(targetScale = 1.1f)
)

@Immutable
data class PageEffects(
    val backgroundEffect : BackgroundEffect,
    val foregroundEffect : ForegroundEffect,
)

val DefaultEffects = PageEffects(
    backgroundEffect = BackgroundEffect(),
    foregroundEffect = ForegroundEffect()
)