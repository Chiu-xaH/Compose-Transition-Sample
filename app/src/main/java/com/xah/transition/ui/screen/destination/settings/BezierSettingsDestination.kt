package com.xah.transition.ui.screen.destination.settings

import androidx.compose.runtime.Composable
import com.xah.transition.R
import com.xah.transition.ui.screen.BezierSettingsScreen
import com.xah.transition.ui.uitls.NavDestination

data object BezierSettingsDestination : NavDestination() {

    override val title: String = "动画曲线设置"
    override val key = "settings_bezier"
    override val icon = R.drawable.animation

    @Composable
    override fun Content() {
        BezierSettingsScreen(title)
    }
}