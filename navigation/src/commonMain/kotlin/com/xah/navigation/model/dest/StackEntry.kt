package com.xah.navigation.model.dest

import com.xah.navigation.model.anim.TransitionEffect
import java.util.UUID

class StackEntry(
    val destination: Destination,
    val transitionMode: TransitionEffect
) {
    var id : String = initId()
        private set

    fun resetState() {
        id = initId()
    }

    private fun initId() = UUID.randomUUID().toString()

    override fun toString(): String {
        return "StackEntry(id=$id, destination_key=${destination.key})"
    }
}