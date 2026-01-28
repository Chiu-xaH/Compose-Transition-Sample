package com.xah.transition.ui.model

sealed interface NavCommand {
    data class Push(
        val destination: Destination
    ) : NavCommand

    object Pop : NavCommand
}
