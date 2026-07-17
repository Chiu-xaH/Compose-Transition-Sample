package com.xah.transition

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.xah.transition.ui.screen.Main

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Transition",
        alwaysOnTop = true
    ) {
        Main()
    }
}