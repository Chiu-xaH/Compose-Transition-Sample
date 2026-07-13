package com.xah.container.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.layer.GraphicsLayer
import com.xah.container.model.ExtensionDirection

actual fun Modifier.pixelExtension(
    parentGraphicsLayer: GraphicsLayer,
    parentRect: Rect?,
    direction: ExtensionDirection
): Modifier = this