package com.xah.transition.ui.screen.destination

import androidx.compose.runtime.Composable
import com.xah.transition.ui.screen.SecondScreen
import com.xah.transition.ui.util.NavDestination

data class SecondDestination(
    val userId: Int,
    val isLong : Boolean
) : NavDestination() {

    override val title: String = "二级界面"
    override val key = "second_${userId}_$isLong"

    @Composable
    override fun Content() {
        SecondScreen()
    }
}