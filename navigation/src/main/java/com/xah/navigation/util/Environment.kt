package com.xah.navigation.util

import androidx.compose.runtime.staticCompositionLocalOf
import com.xah.navigation.controller.NavStackState

// 根导航
val LocalNavStackState = staticCompositionLocalOf<NavStackState> {
    error("未提供根NavStackState")
}