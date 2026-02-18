package com.xah.container.logic

import androidx.compose.animation.core.spring
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
        val rectFrom = state.layoutRect ?: return

        state.isRunning = true

        onSwapContent()
        awaitFrame()

        val rectTo = state.layoutRect ?: return

        state.rectFrom = rectFrom
        state.rectTo = rectTo
        state.targetRect = state.rectFrom

        state.animation.snapTo(0f)
        state.animation.animateTo(1f,testSpring)

        state.isRunning = false
    }

    suspend fun pop(
        key: Any,
        onSwapContent: suspend () -> Unit
    ) {

        val state = states[key] ?: return
        val rectFrom = state.layoutRect ?: return

        state.isRunning = true

        onSwapContent()
        awaitFrame()

        val rectTo = state.layoutRect ?: return

        state.rectFrom = rectFrom
        state.rectTo = rectTo
        state.targetRect = state.rectTo

        state.animation.snapTo(0f)
        state.animation.animateTo(1f,testSpring)

        state.isRunning = false
    }
}