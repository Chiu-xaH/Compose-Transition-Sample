package com.xah.container.logic

import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.mutableStateMapOf
import com.xah.container.logic.model.SharedContainerAction
import com.xah.container.logic.model.SharedContainerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch

// TODO 干掉onSwapContent
class SharedContainerRegistry(
    private val scope: CoroutineScope
) {
    val states = mutableStateMapOf<Any, SharedContainerState>()
    val runningStates: List<SharedContainerState>
        get() = states.values.filter { it.isRunning }

    private var runningJob: Job? = null

//        spring<Float>(
//        stiffness = 50f,
//        dampingRatio = 0.8f
//    )

    private val pushAnimation = tween<Float>(800)
    private val popAnimation = pushAnimation

    var rectInterpolator: RectInterpolator = LinearRectInterpolator

    // 渐隐、圆角变化比容器变化时长
    val speedUpRadio = 1.5f

    fun getOrCreate(
        key: Any,
    ): SharedContainerState {
        return states.getOrPut(key) {
            SharedContainerState()
        }
    }

    fun push(
        key: Any,
        onSwapContent: suspend () -> Unit
    ) {
        scope.launch {
            internalPush(key, onSwapContent)
        }
    }

    fun pop(
        key: Any,
        onSwapContent: suspend () -> Unit
    ) {
        scope.launch {
            internalPop(key, onSwapContent)
        }
    }

    private suspend fun internalPush(
        key: Any,
        onSwapContent: suspend () -> Unit
    ) {
        val state = states[key] ?: return
        // 赋值
        state.containerLayout = state.layout
        state.containerRect = state.layoutRect

        // 开始标识位
        state.isRunning = true
        state.action = SharedContainerAction.PUSH

        onSwapContent()
        awaitFrame()

        // 赋值
        state.contentLayout = state.layout
        state.contentRect = state.layoutRect

        state.animation.animateTo(1f,pushAnimation)

        // 结束标志位
        state.isRunning = false
        state.action = SharedContainerAction.NONE
    }

    private suspend fun internalPop(
        key: Any,
        onSwapContent: suspend () -> Unit
    ) {
        val state = states[key] ?: return
        // 赋值 TODO 有个问题
        if(state.contentLayout == null) {
            state.contentLayout = state.layout
        }
        if(state.contentRect == null) {
            state.contentRect = state.layoutRect
        }

        // 开始标识位
        state.isRunning = true
        state.action = SharedContainerAction.POP

        onSwapContent()
        awaitFrame()

        // 赋值 TODO 有个问题
        if(state.containerLayout == null) {
            state.containerLayout = state.layout
        }
        if(state.containerRect == null) {
            state.containerRect = state.layoutRect
        }

        state.animation.animateTo(0f,popAnimation)

        // 结束标志位
        state.isRunning = false
        state.action = SharedContainerAction.NONE
    }
}