package com.xah.transition.ui.screen.nav.destination

import androidx.compose.runtime.Composable
import com.xah.transition.ui.screen.HomeScreen
import com.xah.transition.ui.screen.nav.destination.base.NavDestination
import sharednav.app.generated.resources.Res
import sharednav.app.generated.resources.ic_texture

object HomeDestination : NavDestination() {
    override val key = "home"
    override val title = "主界面"
    override val icon = Res.drawable.ic_texture

    @Composable
    override fun Content() {
        HomeScreen()
    }
}