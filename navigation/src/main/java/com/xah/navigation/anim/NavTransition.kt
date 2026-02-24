package com.xah.navigation.anim

import com.xah.navigation.model.BackStackEntry
import com.xah.navigation.model.NavActionType

data class NavTransition(
    val type: NavActionType,
    val from: BackStackEntry,
    val to: BackStackEntry,
)