package com.xah.transition.ui.screen.destination

import androidx.compose.runtime.Composable
import com.xah.navigation.model.Destination
import com.xah.transition.ui.screen.AppBean
import com.xah.transition.ui.screen.AppHomeScreen

data class AppHomeDestination(
    val app: AppBean
) : Destination {

    override val description: String = app.name
    override val key = "app_home_${app.key}"

    @Composable
    override fun Content() {
        AppHomeScreen(app)
    }
}