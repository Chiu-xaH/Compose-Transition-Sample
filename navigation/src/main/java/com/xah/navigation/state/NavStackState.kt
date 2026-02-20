package com.xah.navigation.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.xah.navigation.model.BackStackEntry
import com.xah.navigation.model.Destination
import com.xah.navigation.model.NavActionType
import com.xah.navigation.effect.NavTransition
import java.util.UUID


class NavStackState(
    startDestination: Destination
) {
    private val _stack = mutableStateListOf<BackStackEntry>()
    val stack: List<BackStackEntry> get() = _stack

    var navTransition by mutableStateOf<NavTransition?>(null)
        private set

    var isTransitioning by mutableStateOf(false)
        private set

    var predictiveProgress by mutableFloatStateOf(0f)
        private set

    fun push(destination: Destination) {
        val from = _stack.last()
        val newEntry = BackStackEntry(
            id = UUID.randomUUID().toString(),
            destination = destination
        )

        _stack += newEntry

        navTransition = NavTransition(
            type = NavActionType.PUSH,
            from = from,
            to = newEntry
        )
        isTransitioning = true
    }

    fun pop() {
        if (_stack.size <= 1) return

        val from = _stack.last()
        val to = _stack[_stack.lastIndex - 1]

        navTransition = NavTransition(
            type = NavActionType.POP,
            from = from,
            to = to,
        )
        isTransitioning = true
    }

    fun onTransitionFinished() {
        when (navTransition?.type) {
            NavActionType.PUSH -> Unit
            NavActionType.POP -> {
                _stack.removeAt(_stack.size-1)
            }
            null -> Unit
        }
        navTransition = null
        isTransitioning = false
    }

    init {
        val rootEntry = BackStackEntry(
            id = UUID.randomUUID().toString(),
            destination = startDestination
        )
        _stack += rootEntry
    }
}