package com.xah.transition.ui.util

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object UiHolder {
    var imageBitmap by mutableStateOf<Bitmap?>(null)
}