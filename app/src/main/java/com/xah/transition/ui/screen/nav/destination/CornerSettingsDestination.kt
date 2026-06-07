package com.xah.transition.ui.screen.nav.destination

import androidx.compose.runtime.Composable
import com.xah.transition.R
import com.xah.transition.ui.screen.CornerSettingsScreen
import com.xah.transition.ui.util.NavDestination

data object CornerSettingsDestination : NavDestination() {

    override val title: String = "屏幕圆角校正"
    override val key = "settings_corner"
    override val icon = R.drawable.ic_rounded_corner

    @Composable
    override fun Content() {
        CornerSettingsScreen(title)
    }
}