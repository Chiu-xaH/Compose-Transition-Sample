package com.xah.navigation.utils

import androidx.compose.runtime.staticCompositionLocalOf
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.Destination

// 根导航
val LocalNavigationController = staticCompositionLocalOf<NavigationController> {
    error("未提供根NavController")
}

val LocalNavigationDestination = staticCompositionLocalOf<Destination> {
    error("未提供根Destination")
}