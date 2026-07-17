package com.sharednav.common.helper

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

actual class ScreenCornerHelper {
    actual fun getCornerDp(): Dp = corner

    actual companion object {
        actual var corner: Dp = 0.dp
        actual val CAN_GET: Boolean = false
    }
}   