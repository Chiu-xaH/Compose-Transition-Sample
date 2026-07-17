package com.sharednav.common.helper

import androidx.compose.ui.unit.Dp

expect class ScreenCornerHelper {
    companion object {
        var corner: Dp
        val CAN_GET: Boolean
    }
    fun getCornerDp(): Dp
}

