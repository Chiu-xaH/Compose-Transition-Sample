package com.xah.transition.ui.screen.destination

import androidx.compose.runtime.Composable
import com.xah.navigation.model.Destination
import com.xah.transition.R
import com.xah.transition.ui.screen.CornerSettingsScreen

data object CornerSettingsDestination : Destination() {

    override val title: String = "屏幕圆角校正"
    override val key = "settings_corner"
    override val icon = R.drawable.rounded_corner

    @Composable
    override fun Content() {
        CornerSettingsScreen()
    }
}