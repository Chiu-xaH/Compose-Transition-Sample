package com.xah.floating.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

// 状态栏反色控制（浮窗）
@Composable
fun SystemBarColorForFloatingWindow() {
    val floatingController = LocalFloatingController.current
    val need = floatingController.backgroundEffect.pageEffect.mask >= 0.5f
    if(!need) {
        return
    }

    val systemUiController = rememberSystemUiController()
    val running = floatingController.isRunning
    val inDark = isSystemInDarkTheme()

    LaunchedEffect(running) {
        if(running) {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = false,
                isNavigationBarContrastEnforced = false
            )
        } else {
            // 复原
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = !inDark,
                isNavigationBarContrastEnforced = false
            )
        }
    }
}