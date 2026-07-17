package com.xah.container.component.overlay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import com.sharednav.common.helper.ScreenCornerHelper

@Composable
actual fun ScreenCornerInit() {
    val view = LocalView.current
    LaunchedEffect(view) {
        ScreenCornerHelper(view)
    }
}