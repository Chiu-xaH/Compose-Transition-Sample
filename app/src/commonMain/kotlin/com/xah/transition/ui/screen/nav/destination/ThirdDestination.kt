package com.xah.transition.ui.screen.nav.destination

import androidx.compose.runtime.Composable
import com.xah.transition.ui.screen.ThirdScreen
import com.xah.transition.ui.screen.nav.destination.base.NavDestination
import sharednav.app.generated.resources.Res
import sharednav.app.generated.resources.ic_texture

object ThirdDestination : NavDestination() {

    override val title: String = "三级界面"
    override val key = "third"
    override val icon = Res.drawable.ic_texture

    @Composable
    override fun Content() {
        ThirdScreen()
    }
}