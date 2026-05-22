package com.xah.floating.anim

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import com.sharednav.common.modifier.mask
import com.sharednav.common.modifier.scaleMirror
import com.xah.floating.model.anim.PageEffect

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

internal fun Modifier.backgroundEffect(
    enableShader : Boolean,
    enableBlur : Boolean,
    effect: PageEffect
) : Modifier {
    return this
        .mask(effect)
        .blur(enableBlur,effect)
        .scale(enableShader,effect)
}