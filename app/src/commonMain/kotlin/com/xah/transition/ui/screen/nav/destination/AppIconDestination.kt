package com.xah.transition.ui.screen.nav.destination

import androidx.compose.runtime.Composable
import com.xah.transition.model.AppIconBean
import com.xah.transition.ui.screen.SecondScreen
import com.xah.transition.ui.util.NavDestination

data class AppIconDestination(
    val app: AppIconBean
) : NavDestination() {

    override val title: String = app.name
    override val key = "app_home_${app.key}"

    @Composable
    override fun Content() {
        SecondScreen()
    }
}