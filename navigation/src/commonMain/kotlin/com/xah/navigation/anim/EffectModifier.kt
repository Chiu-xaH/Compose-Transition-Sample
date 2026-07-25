package com.xah.navigation.anim

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import com.sharednav.common.modifier.mask
import com.sharednav.common.modifier.scaleMirror
import com.xah.navigation.model.anim.effect.PageEffectFrame
import com.xah.navigation.model.anim.effect.sub.BgEffectBackground
import com.xah.navigation.model.anim.effect.sub.Roll
import com.xah.navigation.model.anim.effect.sub.RollShape

private fun Modifier.mask(effect: PageEffectFrame) : Modifier = composed {
    this.mask(
        if(isSystemInDarkTheme()) {
            effect.maskDark
        } else {
            effect.maskLight
        }
    )
}

private fun Modifier.blur(enableBlur : Boolean,effect : PageEffectFrame) : Modifier {
    return if(enableBlur) {
        this.blur(effect.blur)
    } else {
        this
    }
}

private fun Modifier.innerBlur(enableBlur : Boolean,effect : PageEffectFrame) : Modifier {
    return if(enableBlur) {
        this.blur(effect.innerBlur)
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

internal fun Modifier.backgroundEffect(
    enableShader : Boolean,
    enableBlur : Boolean,
    background : BgEffectBackground?,
    effect: PageEffectFrame
) : Modifier = this
    // 背景色
    .let {
        when(background) {
            null -> it
            is BgEffectBackground.Image -> {
                it
                    .paint(
                        painter = BitmapPainter(background.bitmap),
                        contentScale = ContentScale.Crop
                    )
                    .background(background.mask)
            }
            is BgEffectBackground.Color -> {
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

internal fun Modifier.foregroundEffect(
    enableBlur : Boolean,
    enableShader: Boolean,
    effect: PageEffectFrame,
) : Modifier = this
    // 模糊
    .blur(enableBlur,effect)
    .graphicsLayer {
        // 形状裁剪
        clip = true
        shape = if (effect.roll != Roll.None) {
            RollShape(effect.roll, effect.corner)
        } else {
            effect.corner
        }

        // 大小
        if(!enableShader) {
            scaleX = effect.scale
            scaleY = effect.scale
        }

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
    .let {
        if(enableShader) {
            it.scale(true,effect)
        } else {
            it
        }
    }
    // 压暗
    .mask(effect)

internal fun Modifier.innerEffect(
    enableBlur : Boolean,
    enableShader: Boolean,
    effect: PageEffectFrame,
) : Modifier = this
    // 模糊
    .innerBlur(enableBlur,effect)

typealias CustomEffectModifier = (progress: Float) -> Modifier