package com.xah.transition.ui.screen.nav.destination

import androidx.compose.runtime.Composable
import com.xah.transition.ui.screen.BezierSettingsScreen
import com.xah.transition.ui.util.NavDestination
import sharednav.app.generated.resources.Res
import sharednav.app.generated.resources.ic_animation

data object BezierSettingsDestination : NavDestination() {

    override val title: String = "动画曲线设置"
    override val key = "settings_bezier"
    override val icon = Res.drawable.ic_animation

    @Composable
    override fun Content() {
        BezierSettingsScreen(title)
    }
}