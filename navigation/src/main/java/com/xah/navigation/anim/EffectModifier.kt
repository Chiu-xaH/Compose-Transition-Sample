package com.xah.navigation.anim

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import com.sharednav.common.modifier.mask
import com.sharednav.common.modifier.scaleMirror
import com.xah.navigation.model.anim.PageEffect

private fun Modifier.mask(effect: PageEffect) : Modifier {
    return this.mask(Color.Black.copy(alpha = effect.mask))
}

private fun Modifier.blur(enableBlur : Boolean,effect : PageEffect) : Modifier {
    return if(enableBlur) {
        this.blur(effect.blur)
    } else {
        this
    }
}

private fun Modifier.scale(
    enableShader : Boolean,
    effect: PageEffect
) : Modifier {
    return this.scaleMirror(effect.scale,enableShader)
}

fun Modifier.backgroundEffect(
    enableShader : Boolean,
    enableBlur : Boolean,
    effect: PageEffect
) : Modifier = this
    .mask(effect)
    .blur(enableBlur,effect)
    .graphicsLayer {
        alpha = effect.alpha

        transformOrigin = effect.position

        translationX = effect.translationPercent.x * size.width
        translationY = effect.translationPercent.y * size.height
    }
    .scale(enableShader,effect)
    .clip(effect.corner)

fun Modifier.foregroundEffect(
    enableBlur : Boolean,
    effect: PageEffect,
) : Modifier = this
    .blur(enableBlur,effect)
    .graphicsLayer {
        clip = true
        shape = effect.corner

        scaleX = effect.scale
        scaleY = effect.scale

        alpha = effect.alpha

        transformOrigin = effect.position

        translationX = effect.translationPercent.x * size.width
        translationY = effect.translationPercent.y * size.height
    }
    .mask(effect)