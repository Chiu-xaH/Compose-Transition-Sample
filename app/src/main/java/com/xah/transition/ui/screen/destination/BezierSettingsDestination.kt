package com.xah.transition.ui.screen.destination

import androidx.compose.runtime.Composable
import com.xah.navigation.model.Destination
import com.xah.transition.R
import com.xah.transition.ui.screen.BezierSettingsScreen


data object BezierSettingsDestination : Destination() {

    override val title: String = "动画曲线设置"
    override val key = "settings_bezier"
    override val icon = R.drawable.animation

    @Composable
    override fun Content() {
        BezierSettingsScreen()
    }
}