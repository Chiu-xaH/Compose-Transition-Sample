package com.xah.floating.util

import androidx.compose.runtime.staticCompositionLocalOf
import com.xah.floating.controller.FloatingController

val LocalFloatingControllerSafely = staticCompositionLocalOf<FloatingController?> { null }

val LocalFloatingController = staticCompositionLocalOf<FloatingController> {
    error("未提供 FloatingController，请在树根处放置 FloatingRoot {}")
}

