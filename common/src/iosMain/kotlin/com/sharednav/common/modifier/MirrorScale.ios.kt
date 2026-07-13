package com.sharednav.common.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

actual fun Modifier.scaleMirror(
    scale: Float,
    enabled: Boolean
): Modifier =
    if(scale == 1f) {
        this
    } else {
        this.graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
    }