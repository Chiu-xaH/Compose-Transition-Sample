package com.xah.transition.ui.screen.destination

import androidx.compose.runtime.Composable
import com.xah.navigation.model.Destination
import com.xah.transition.ui.screen.ThirdScreen

object ThirdDestination : Destination() {

    override val title: String = "Third"
    override val key = "third"

    @Composable
    override fun Content() {
        ThirdScreen()
    }
}