package com.xah.container.controller

import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.xah.container.model.SharedContainerState
import com.xah.container.anim.LinearRectInterpolator
import com.xah.container.anim.RectInterpolator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch

class SharedContainerRegistry(
    private val scope: CoroutineScope
) {
    val states = mutableStateMapOf<Any, SharedContainerState>()
    val runningStates: List<SharedContainerState>
        get() = states.values.filter { it.isRunning }
    val isRunning: Boolean
        get() = states.values.any { it.isRunning }

    var enabled by mutableStateOf(true)

    private val pushAnimation = tween<Float>(500)
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
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwapContent: suspend () -> Unit
    ) {
        scope.launch {
            if(!enabled) {
                onSwapContent()
                return@launch
            }
            internalPush(key,onAnimatedFinished, onSwapContent)
        }
    }

    fun pop(
        key: Any,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwapContent: suspend () -> Unit
    ) {
        scope.launch {
            if(!enabled) {
                onSwapContent()
                return@launch
            }
            internalPop(key, onAnimatedFinished,onSwapContent)
        }
    }

    private suspend fun internalPush(
        key: Any,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwapContent: suspend () -> Unit
    ) {
        val state = states[key] ?: return

        onSwapContent()
        awaitFrame()

        // 开始标识位
        state.isRunning = true

        state.animation.animateTo(1f,pushAnimation)
        onAnimatedFinished?.let { it() }
        // 结束标志位
        state.isRunning = false
    }

    private suspend fun internalPop(
        key: Any,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwapContent: suspend () -> Unit
    ) {
        val state = states[key] ?: return

        onSwapContent()
        awaitFrame()
        // 开始标识位
        state.isRunning = true

        state.animation.animateTo(0f,popAnimation)

        onAnimatedFinished?.let { it() }
        // 结束标志位
        state.isRunning = false
    }
}