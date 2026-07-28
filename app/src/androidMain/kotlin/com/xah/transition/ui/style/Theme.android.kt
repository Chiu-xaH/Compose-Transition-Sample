package com.xah.transition.ui.style

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import com.sharednav.common.kmp.PlatformContext

actual val CAN_DYNAMIC_COLOR = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

actual fun getColorScheme(darkTheme: Boolean,context : PlatformContext): ColorScheme = when {
    CAN_DYNAMIC_COLOR -> {
        if (darkTheme) {
            dynamicDarkColorScheme(context.context)
        } else {
            dynamicLightColorScheme(context.context)
        }
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
}