package com.xah.navigation.model

import androidx.compose.animation.core.MutableTransitionState

class BackStackEntry(
    val id: String,
    val destination: Destination
) {
    val transitionState = MutableTransitionState(NavPhase.Entering)
}