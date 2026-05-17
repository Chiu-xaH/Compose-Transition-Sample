package com.xah.navigation.anim

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import com.sharednav.common.modifier.mask
import com.sharednav.common.modifier.scaleMirror
import com.xah.navigation.model.anim.BackgroundEffectBg
import com.xah.navigation.model.anim.PageEffectFrame

private fun Modifier.mask(effect: PageEffectFrame) : Modifier = this.mask(effect.mask)

private fun Modifier.blur(enableBlur : Boolean,effect : PageEffectFrame) : Modifier {
    return if(enableBlur) {
        this.blur(effect.blur)
    } else {
        this
    }
}

private fun Modifier.scale(
    enableShader : Boolean,
    effect: PageEffectFrame
) : Modifier {
    return this.scaleMirror(effect.scale,enableShader)
}

fun Modifier.backgroundEffect(
    enableShader : Boolean,
    enableBlur : Boolean,
    background : BackgroundEffectBg?,
    effect: PageEffectFrame
) : Modifier = this
    // 背景色
    .let {
        when(background) {
            null -> it
            is BackgroundEffectBg.Image -> {
                it
                    .paint(
                        painter = BitmapPainter(background.bitmap.asImageBitmap()),
                        contentScale = ContentScale.Crop
                    )
                    .background(background.mask)
            }
            is BackgroundEffectBg.Color -> {
                it.background(background.color)
            }
        }
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
    effect: PageEffectFrame,
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