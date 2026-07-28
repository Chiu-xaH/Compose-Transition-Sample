package com.xah.transition.ui.holder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import com.sharednav.common.helper.EnableHelper
import com.xah.shader.state.ShaderState

object GlobalUiStateHolder {
    var imageBitmap by mutableStateOf<ImageBitmap?>(null)

    var enablePredictiveBack by mutableStateOf(EnableHelper.canPredictedGesture)
    var enableWallpaper by mutableStateOf(false)

    var shaderState by mutableStateOf<ShaderState?>(null)
}