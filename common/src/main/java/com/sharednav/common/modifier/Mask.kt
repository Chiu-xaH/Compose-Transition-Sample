package com.sharednav.common.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color

val defaultMask = Color.Black.copy(.25f)
val noneMask = Color.Transparent

fun Modifier.mask(color : Color) : Modifier {
    return this.drawWithCache {
        onDrawWithContent {
            drawContent()
            drawRect(color)
        }
    }
}