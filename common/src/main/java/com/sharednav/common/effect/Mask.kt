package com.sharednav.common.effect

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color

fun Modifier.mask(color : Color) : Modifier {
    return this.drawWithCache {
        onDrawWithContent {
            drawContent()
            drawRect(color)
        }
    }
}