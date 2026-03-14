package com.xah.transition.ui.screen.destination

import androidx.compose.runtime.Composable
import com.xah.transition.ui.screen.HomeScreen
import com.xah.transition.ui.util.NavDestination

object HomeDestination : NavDestination() {
    override val key = "home"
    override val title: String = "主界面"

    @Composable
    override fun Content() {
        HomeScreen()
    }
}