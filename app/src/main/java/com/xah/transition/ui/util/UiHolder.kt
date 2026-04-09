package com.xah.transition.ui.util

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

object UiHolder {
    var imageBitmap by mutableStateOf<Bitmap?>(null)

    var enablePredictiveBack by mutableStateOf(Build.VERSION.SDK_INT >= 33)
}