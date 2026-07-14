package com.xah.transition

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.transition.ui.screen.Main

fun MainViewController(cornerRadius: Float = 0f) = ComposeUIViewController {
    ScreenCornerHelper.corner = cornerRadius.dp
    Main()
}
