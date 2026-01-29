package com.xah.transition.ui.screen

import androidx.compose.runtime.Composable
import com.xah.transition.ui.model.Destination

object HomeDestination : Destination {
    override val key = "home"
    override val description: String = "Home"

    @Composable
    override fun Content() {
        HomeScreen()
    }
}