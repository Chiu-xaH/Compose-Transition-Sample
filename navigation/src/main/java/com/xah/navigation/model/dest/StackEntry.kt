package com.xah.navigation.model.dest

class StackEntry(
    val id: String,
    val destination: Destination,
) {
    override fun toString(): String {
        return "StackEntry(id=$id, destination_key=${destination.key})"
    }
}