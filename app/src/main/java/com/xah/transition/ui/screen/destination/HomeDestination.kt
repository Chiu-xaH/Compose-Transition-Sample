package com.xah.transition.ui.screen.destination

import androidx.compose.runtime.Composable
import com.xah.navigation.model.Destination
import com.xah.transition.ui.screen.HomeScreen

object HomeDestination : Destination() {
    override val key = "home"
    override val title: String = "主界面"

    @Composable
    override fun Content() {
        HomeScreen()
    }
}