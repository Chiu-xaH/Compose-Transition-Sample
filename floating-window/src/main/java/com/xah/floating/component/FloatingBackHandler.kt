package com.xah.floating.component

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.xah.floating.util.LocalFloatingController

@Composable
fun FloatingBackHandler() {
    val controller = LocalFloatingController.current
    BackHandler(enabled = controller.isRunning) {
        controller.pop()
    }
}
