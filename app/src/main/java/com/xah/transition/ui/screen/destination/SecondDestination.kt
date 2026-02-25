package com.xah.transition.ui.screen.destination

import androidx.compose.runtime.Composable
import com.xah.navigation.model.Destination
import com.xah.transition.ui.screen.SecondScreen

data class SecondDestination(
    val userId: Int
) : Destination() {

    override val title: String = "Second"
    override val key = "second_$userId"

    @Composable
    override fun Content() {
        SecondScreen(userId)
    }
}