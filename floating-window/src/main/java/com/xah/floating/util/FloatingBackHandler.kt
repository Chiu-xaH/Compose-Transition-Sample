package com.xah.floating.util

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
fun FloatingBackHandler() {
    val controller = LocalFloatingController.current
    BackHandler(enabled = controller.isRunning) {
        controller.pop()
    }
}
