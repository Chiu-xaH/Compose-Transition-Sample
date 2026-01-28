package com.xah.transition.ui.state

import androidx.compose.runtime.staticCompositionLocalOf
import com.xah.transition.ui.NavStackState

// 根导航
val LocalNavStackState = staticCompositionLocalOf<NavStackState> {
    error("未提供根NavStackState")
}