package com.xah.transition.ui.screen.destination

import androidx.compose.runtime.Composable
import com.xah.transition.ui.screen.AppBean
import com.xah.transition.ui.screen.AppHomeScreen
import com.xah.transition.ui.uitls.NavDestination

data class AppHomeDestination(
    val app: AppBean
) : NavDestination() {

    override val title: String = app.name
    override val key = "app_home_${app.key}"

    @Composable
    override fun Content() {
        AppHomeScreen()
    }
}