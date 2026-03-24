package com.xah.navigation.model.anim

import com.xah.navigation.model.action.ActionType
import com.xah.navigation.model.dest.StackEntry

data class Transition(
    val type: ActionType,
    val from: StackEntry,
    val to: StackEntry,
) {
    override fun toString(): String {
        return "(type=${type.name},from=${from},to=${to})"
    }
}