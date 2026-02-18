package com.xah.container

import androidx.compose.animation.core.spring
import androidx.compose.runtime.staticCompositionLocalOf
import kotlinx.coroutines.android.awaitFrame

class SharedContainerRegistry {

    val states = mutableMapOf<Any, SharedContainerState>()
    val runningStates: List<SharedContainerState>
        get() = states.values.filter { it.isRunning }

    private val testSpring = spring<Float>(
        stiffness = 100f,
    )

    fun getOrCreate(
        key: Any,
    ): SharedContainerState {
        return states.getOrPut(key) {
            SharedContainerState()
        }
    }

    suspend fun push(
        key: Any,
        onSwapContent: suspend () -> Unit
    ) {

        val state = states[key] ?: return
        val from = state.layoutRect ?: return

        state.isRunning = true

        onSwapContent()
        awaitFrame()

        val to = state.layoutRect ?: return

        state.transitionFrom = from
        state.transitionTo = to

        state.animation.snapTo(0f)
        state.animation.animateTo(1f,testSpring)

        state.isRunning = false
    }

    suspend fun pop(
        key: Any,
        onSwapContent: suspend () -> Unit
    ) {

        val state = states[key] ?: return
        val from = state.layoutRect ?: return

        state.isRunning = true

        onSwapContent()
        awaitFrame()

        val to = state.layoutRect ?: return

        state.transitionFrom = from
        state.transitionTo = to

        state.animation.snapTo(0f)
        state.animation.animateTo(1f,testSpring)

        state.isRunning = false
    }
}


val LocalSharedContainerRegistry = staticCompositionLocalOf<SharedContainerRegistry> {
    error("未提供SharedContainerRegistry,请确认是否使用了本Library的SharedContainerRoot")
}