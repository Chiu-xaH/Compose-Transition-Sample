package com.xah.navigation.model.anim.effect.sub

import android.graphics.Bitmap
import com.sharednav.common.modifier.defaultMask

sealed class BgEffectBackground {
    data class Image(
        val bitmap : Bitmap,
        val mask : androidx.compose.ui.graphics.Color = defaultMask
    ) : BgEffectBackground()

    data class Color(val color : androidx.compose.ui.graphics.Color) : BgEffectBackground()
}