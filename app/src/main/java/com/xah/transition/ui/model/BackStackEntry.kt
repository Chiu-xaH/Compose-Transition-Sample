package com.xah.transition.ui.model

import android.accessibilityservice.GestureDescription
import androidx.compose.animation.core.MutableTransitionState

class BackStackEntry(
    val id: String,
    val destination: Destination
) {
    val transitionState = MutableTransitionState(NavPhase.Entering)
}

