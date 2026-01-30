package com.xah.transition.ui.screen

import androidx.compose.runtime.Composable
import com.xah.transition.ui.model.Destination

object ThirdDestination : Destination {

    override val description: String = "Third"
    override val key = "third"

    @Composable
    override fun Content() {
        ThirdScreen()
    }
}