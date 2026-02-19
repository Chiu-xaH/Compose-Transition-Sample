package com.xah.container.logic

import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import com.xah.container.logic.model.ShardContainerAction
import com.xah.container.logic.model.SharedContainerState
import kotlinx.coroutines.android.awaitFrame

class SharedContainerRegistry {

    val states = mutableMapOf<Any, SharedContainerState>()
    val runningStates: List<SharedContainerState>
        get() = states.values.filter { it.isRunning }

    private val popAnimation = spring<Float>(
        stiffness = 50f,
        dampingRatio = 0.8f
    )

    private val pushAnimation = tween<Float>(800)

    // 渐隐、圆角变化比容器变化时长
    val speedUpRadio = 1.25f

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
        state.contentContainer = state.content

        state.isRunning = true
        state.action = ShardContainerAction.PUSH

        onSwapContent()
        awaitFrame()

        val rectTo = state.layoutRect ?: return
        state.contentContent = state.content

        state.rectContainer = rectFrom
        state.rectContent = rectTo

        state.animation.snapTo(0f)
        state.animation.animateTo(1f,pushAnimation)

        state.isRunning = false
        state.action = ShardContainerAction.NONE
    }

    suspend fun pop(
        key: Any,
        onSwapContent: suspend () -> Unit
    ) {

        val state = states[key] ?: return
        val rectFrom = state.layoutRect ?: return
        state.contentContent = state.content

        state.isRunning = true
        state.action = ShardContainerAction.POP

        onSwapContent()
        awaitFrame()

        val rectTo = state.layoutRect ?: return
        state.contentContainer = state.content

        state.rectContainer = rectFrom
        state.rectContent = rectTo

        state.animation.snapTo(0f)
        state.animation.animateTo(1f,popAnimation)

        state.isRunning = false
        state.action = ShardContainerAction.NONE
    }
}