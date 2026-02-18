package com.xah.container.ui.util

import androidx.compose.runtime.staticCompositionLocalOf
import com.xah.container.logic.SharedContainerRegistry


val LocalSharedContainerRegistry = staticCompositionLocalOf<SharedContainerRegistry> {
    error("未提供SharedContainerRegistry,请确认是否使用了本Library的SharedContainerRoot")
}