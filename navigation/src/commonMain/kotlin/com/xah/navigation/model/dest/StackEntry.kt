package com.xah.navigation.model.dest

import com.sharednav.common.helper.randomUUID
import com.xah.navigation.model.anim.TransitionEffect

class StackEntry(
    val destination: Destination,
    val transitionMode: TransitionEffect
) {
    var id : String = initId()
        private set

    fun resetState() {
        id = initId()
    }

    private fun initId() = randomUUID()

    override fun toString(): String {
        return "StackEntry(id=$id, destination_key=${destination.key})"
    }
}