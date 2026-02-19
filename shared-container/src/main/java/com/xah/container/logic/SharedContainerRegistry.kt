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
        // 赋值
        state.containerLayout = state.layout
        state.containerRect = state.layoutRect

        // 开始标识位
        state.isRunning = true
        state.action = ShardContainerAction.PUSH

        onSwapContent()
        awaitFrame()

        // 赋值
        state.contentLayout = state.layout
        state.contentRect = state.layoutRect

        state.animation.animateTo(1f,pushAnimation)

        // 结束标志位
        state.isRunning = false
        state.action = ShardContainerAction.NONE
    }

    suspend fun pop(
        key: Any,
        onSwapContent: suspend () -> Unit
    ) {
        val state = states[key] ?: return
        // 赋值
        state.contentLayout = state.layout
        state.contentRect = state.layoutRect

        // 开始标识位
        state.isRunning = true
        state.action = ShardContainerAction.POP

        onSwapContent()
        awaitFrame()

        // 赋值
        state.containerLayout = state.layout
        state.containerRect = state.layoutRect

        state.animation.animateTo(0f,popAnimation)

        // 结束标志位
        state.isRunning = false
        state.action = ShardContainerAction.NONE
    }
}