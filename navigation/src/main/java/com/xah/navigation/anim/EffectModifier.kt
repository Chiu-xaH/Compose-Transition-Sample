package com.xah.navigation.anim

import androidx.compose.foundation.background
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
    background : Color?,
    effect: PageEffect
) : Modifier = this
    // 背景色
    .let {
        background?.let { bg ->
            it.background(bg)
        } ?: it
    }
    // 压暗
    .mask(effect)
    // 模糊
    .blur(enableBlur,effect)
    .graphicsLayer {
        // 透明度
        alpha = effect.alpha

        // 位移
        translationX = effect.translationPercent.x * size.width
        translationY = effect.translationPercent.y * size.height
        transformOrigin = effect.position

        // 旋转
        rotationX = effect.rotate.x
        rotationY = effect.rotate.y
        rotationZ = effect.rotate.z
    }
    // 大小
    .scale(enableShader,effect)
    // 圆角
    .clip(effect.corner)

fun Modifier.foregroundEffect(
    enableBlur : Boolean,
    effect: PageEffect,
) : Modifier = this
    // 模糊
    .blur(enableBlur,effect)
    .graphicsLayer {
        // 圆角
        clip = true
        shape = effect.corner

        // 大小
        scaleX = effect.scale
        scaleY = effect.scale

        // 透明度
        alpha = effect.alpha

        // 位移
        translationX = effect.translationPercent.x * size.width
        translationY = effect.translationPercent.y * size.height
        transformOrigin = effect.position

        // 旋转
        rotationX = effect.rotate.x
        rotationY = effect.rotate.y
        rotationZ = effect.rotate.z
    }
    // 压暗
    .mask(effect)