package com.xah.navigation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.Destination
import com.xah.navigation.model.NavDependencies

// 根导航
val LocalNavigationController = staticCompositionLocalOf<NavigationController> {
    error("未提供根NavController")
}

val LocalNavigationDestination = staticCompositionLocalOf<Destination> {
    error("未提供根Destination")
}

val LocalNavDependencies = compositionLocalOf { NavDependencies() }

@Composable
fun rememberNavDependencies(
    vararg keys: Any?,
    builder: NavDependencies.() -> Unit
): NavDependencies {
    return remember(keys) {
        NavDependencies().apply(builder)
    }
}