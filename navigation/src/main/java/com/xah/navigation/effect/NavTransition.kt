package com.xah.navigation.effect

import com.xah.navigation.model.BackStackEntry
import com.xah.navigation.model.NavActionType

data class NavTransition(
    val type: NavActionType,        // Push / Pop
    val from: BackStackEntry,
    val to: BackStackEntry,
)