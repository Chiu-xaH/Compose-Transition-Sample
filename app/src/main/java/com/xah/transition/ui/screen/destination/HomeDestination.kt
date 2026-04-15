package com.xah.transition.ui.screen.destination

import androidx.compose.runtime.Composable
import com.xah.transition.R
import com.xah.transition.ui.screen.HomeScreen
import com.xah.transition.ui.util.NavDestination

object HomeDestination : NavDestination() {
    override val key = "home"
    override val title = "主界面"
    override val icon = R.drawable.ic_texture

    @Composable
    override fun Content() {
        HomeScreen()
    }
}