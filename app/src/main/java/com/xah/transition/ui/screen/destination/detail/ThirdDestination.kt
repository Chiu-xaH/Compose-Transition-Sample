package com.xah.transition.ui.screen.destination.detail

import androidx.compose.runtime.Composable
import com.xah.transition.ui.screen.ThirdScreen
import com.xah.transition.ui.uitls.NavDestination

object ThirdDestination : NavDestination() {

    override val title: String = "三级界面"
    override val key = "third"

    @Composable
    override fun Content() {
        ThirdScreen()
    }
}