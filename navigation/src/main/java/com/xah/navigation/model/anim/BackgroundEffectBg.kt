package com.xah.navigation.model.anim

import android.graphics.Bitmap
import com.sharednav.common.modifier.defaultMask

sealed class BackgroundEffectBg {
    data class Image(
        val bitmap : Bitmap,
        val mask : androidx.compose.ui.graphics.Color = defaultMask
    ) : BackgroundEffectBg()

    data class Color(val color : androidx.compose.ui.graphics.Color) : BackgroundEffectBg()
}