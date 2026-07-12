package com.xah.container.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.layer.GraphicsLayer
import com.xah.container.model.ExtensionDirection


expect fun Modifier.pixelExtension(
    parentGraphicsLayer: GraphicsLayer,
    parentRect: Rect?,
    direction : ExtensionDirection
): Modifier

/**
 * @param isLandscape 是否是横屏，为true则取右侧1像素，否则取底部1像素
 * @param isDouble 是否取双边延展
 */
fun Modifier.pixelExtension(
    parentGraphicsLayer: GraphicsLayer,
    parentRect: Rect?,
    isLandscape : Boolean,
    isDouble : Boolean = false,
): Modifier = pixelExtension(
    parentGraphicsLayer,
    parentRect,
    if(isLandscape) {
        if(!isDouble) {
            ExtensionDirection.END
        } else {
            ExtensionDirection.HORIZONTAL
        }
    } else {
        if(!isDouble) {
            ExtensionDirection.BOTTOM
        } else {
            ExtensionDirection.VERTICAL
        }
    }
)
