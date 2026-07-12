package com.xah.container.util

import androidx.compose.runtime.staticCompositionLocalOf
import com.xah.container.controller.SharedRegistry

val LocalSharedRegistrySafely = staticCompositionLocalOf<SharedRegistry?> {
    null
}

val LocalSharedRegistry = staticCompositionLocalOf<SharedRegistry> {
    error("未提供SharedContainerRegistry,请确认是否使用了本Library的SharedContainerRoot")
}