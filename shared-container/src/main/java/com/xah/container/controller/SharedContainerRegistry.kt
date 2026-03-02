package com.xah.container.controller

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.xah.container.anim.LinearRectInterpolator
import com.xah.container.anim.RectInterpolator
import com.xah.container.model.SharedContainerState
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

    var x1 by mutableFloatStateOf(0.4f)
    var y1 by mutableFloatStateOf(0.0f)
    var x2 by mutableFloatStateOf(0.2f)
    var y2 by mutableFloatStateOf(1f)

    fun getTween() = tween<Float>(500, easing = CubicBezierEasing(x1,y1,x2,y2))
    private val pushAnimation = tween<Float>(500, easing = CubicBezierEasing(0.4f, 0.0f, 0.2f, 1.0f))
    private val popAnimation = pushAnimation

    var rectInterpolator: RectInterpolator = LinearRectInterpolator

    // 渐隐、圆角变化比容器变化时长
    val speedUpRadio = 1.5f

    // 单边填充or双边填充
    var extensionDouble by mutableStateOf(false)

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

    fun push(
        state: SharedContainerState,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwapContent: suspend () -> Unit
    ) {
        scope.launch {
            if(!enabled) {
                onSwapContent()
                return@launch
            }
            internalPush(state,onAnimatedFinished, onSwapContent)
        }
    }

    fun pop(
        state: SharedContainerState,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwapContent: suspend () -> Unit
    ) {
        scope.launch {
            if(!enabled) {
                onSwapContent()
                return@launch
            }
            internalPop(state, onAnimatedFinished,onSwapContent)
        }
    }

    private suspend fun internalPush(
        key: Any,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwapContent: suspend () -> Unit
    ) {
        val state = states[key]
        if(state == null) {
            onSwapContent()
            return
        }
        internalPush(state,onAnimatedFinished,onSwapContent)
    }

    private suspend fun internalPop(
        key: Any,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwapContent: suspend () -> Unit
    ) {
        val state = states[key]
        if(state == null) {
            onSwapContent()
            return
        }
        internalPop(state,onAnimatedFinished,onSwapContent)
    }

    private suspend fun internalPush(
        state: SharedContainerState,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwapContent: suspend () -> Unit
    ) {
        onSwapContent()
        awaitFrame()
        snap(state,true)
        // 开始标识位
        state.isRunning = true

        state.animation.animateTo(1f,getTween())
        onAnimatedFinished?.let { it() }
        // 结束标志位
        state.isRunning = false
    }

    private suspend fun internalPop(
        state: SharedContainerState,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwapContent: suspend () -> Unit
    ) {
        onSwapContent()
        awaitFrame()
        snap(state,false)
        // 开始标识位
        state.isRunning = true

        state.animation.animateTo(0f,getTween())

        onAnimatedFinished?.let { it() }
        // 结束标志位
        state.isRunning = false
    }


    private suspend fun snap(
        state: SharedContainerState,
        isPush : Boolean
    ) {
        if(!state.isRunning) {
            state.animation.snapTo(
                if(isPush) {
                    0f
                } else {
                    1f
                }
            )
        }
    }
}