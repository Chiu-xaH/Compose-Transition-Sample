package com.xah.transition.ui.util

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.sharednav.common.util.EnableHelper

object UiHolder {
    var imageBitmap by mutableStateOf<ImageBitmap?>(null)

    var enablePredictiveBack by mutableStateOf(EnableHelper.canPredictedGesture)
    var enableWallpaper by mutableStateOf(false)
}