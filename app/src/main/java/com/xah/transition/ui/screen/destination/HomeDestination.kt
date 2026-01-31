package com.xah.transition.ui.screen.destination

import androidx.compose.runtime.Composable
import com.xah.navigation.model.Destination
import com.xah.transition.ui.screen.HomeScreen

object HomeDestination : Destination {
    override val key = "home"
    override val description: String = "Home"

    @Composable
    override fun Content() {
        HomeScreen()
    }
}