package com.xah.navigation.anim

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import com.xah.navigation.util.scaleMirror


fun Modifier.mask(color : Color) : Modifier {
    return this.drawWithCache {
        onDrawWithContent {
            drawContent()
            drawRect(color)
        }
    }
}

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

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun Modifier.scale(
    enableShader : Boolean,
    isRegistryRunning : Boolean,
    effect: PageEffect
) : Modifier {
    // fixme:这里用graphicsLayer最后会抽搐一下，太奇怪了，暂时禁用
    if(!enableShader && isRegistryRunning) {
        return this
    } else {
        return this.scaleMirror(effect.scale,enableShader)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
fun Modifier.backgroundEffect(
    enableShader : Boolean,
    enableBlur : Boolean,
    isRegistryRunning : Boolean,
    effect: PageEffect
) : Modifier {
    return this
        .mask(effect)
        .blur(enableBlur,effect)
        .graphicsLayer(alpha = effect.alpha)
        .scale(enableShader,isRegistryRunning,effect)
}


fun Modifier.foregroundEffect(
    enableBlur : Boolean,
    effect: PageEffect,
    foregroundOrigin : TransformOrigin
) : Modifier {
    return this
        .blur(enableBlur,effect)
        .graphicsLayer {
            clip = true
            shape = effect.corner

            scaleX = effect.scale
            scaleY = effect.scale

            alpha = effect.alpha

            transformOrigin = foregroundOrigin
        }
        .mask(effect)
}