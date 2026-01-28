package com.xah.transition.ui.screen

import androidx.compose.runtime.Composable
import com.xah.transition.ui.model.Destination

data class SecondDestination(
    val userId: String
) : Destination {

    override val key = "second_$userId"

    @Composable
    override fun Content() {
        SecondScreen(userId)
    }
}