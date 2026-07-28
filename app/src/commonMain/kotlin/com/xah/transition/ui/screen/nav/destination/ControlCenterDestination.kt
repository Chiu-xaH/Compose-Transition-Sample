package com.xah.transition.ui.screen.nav.destination

import androidx.compose.runtime.Composable
import com.xah.transition.ui.screen.ControlCenterScreen
import com.xah.transition.ui.screen.nav.destination.base.NavDestination
import sharednav.app.generated.resources.Res
import sharednav.app.generated.resources.ic_texture

data object ControlCenterDestination : NavDestination() {

    override val title: String = "启动台"
    override val key = "control_center"
    override val icon = Res.drawable.ic_texture

    @Composable
    override fun Content() {
        ControlCenterScreen()
    }
}