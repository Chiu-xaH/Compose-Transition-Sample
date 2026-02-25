package com.xah.transition.ui.theme

import androidx.compose.runtime.snapshotFlow
import com.xah.container.controller.SharedContainerRegistry
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.Destination
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

fun pop(route : String, navigationController : NavigationController, registry : SharedContainerRegistry) {
    registry.pop(
        route,
        onAnimatedFinished = {
            snapshotFlow { navigationController.isTransitioning }
                .filter { !it }
                .first()
        }
    ) {
        navigationController.pop()
    }
}

fun push(route: String, destination: Destination, navigationController : NavigationController, registry : SharedContainerRegistry) {
    registry.push(
        route,
        onAnimatedFinished = {
            snapshotFlow { navigationController.isTransitioning }
                .filter { !it }
                .first()
        }
    ) {
        navigationController.push(destination)
    }
}
