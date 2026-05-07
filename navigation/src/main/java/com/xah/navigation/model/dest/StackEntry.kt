package com.xah.navigation.model.dest

import com.xah.navigation.model.anim.TransitionEffect

class StackEntry(
    val id: String,
    val destination: Destination,
    val transitionMode: TransitionEffect
) {
    override fun toString(): String {
        return "StackEntry(id=$id, destination_key=${destination.key})"
    }
}