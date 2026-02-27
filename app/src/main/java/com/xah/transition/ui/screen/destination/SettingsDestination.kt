package com.xah.transition.ui.screen.destination

import androidx.compose.runtime.Composable
import com.xah.navigation.model.Destination
import com.xah.transition.ui.screen.SettingsScreen

data class SettingsDestination(val origin : String) : Destination() {

    override val title: String = "设置"
    override val key = "settings_$origin"

    @Composable
    override fun Content() {
        SettingsScreen()
    }
}