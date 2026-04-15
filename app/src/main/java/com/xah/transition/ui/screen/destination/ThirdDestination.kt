package com.xah.transition.ui.screen.destination

import androidx.compose.runtime.Composable
import com.xah.transition.R
import com.xah.transition.ui.screen.ThirdScreen
import com.xah.transition.ui.util.NavDestination

object ThirdDestination : NavDestination() {

    override val title: String = "三级界面"
    override val key = "third"
    override val icon = R.drawable.ic_texture

    @Composable
    override fun Content() {
        ThirdScreen()
    }
}