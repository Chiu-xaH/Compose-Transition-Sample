package com.xah.container.logic

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.xah.common.LogUtil
import com.xah.container.logic.model.SharedContainerState
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
//        spring<Float>(
//        stiffness = 50f,
//        dampingRatio = 0.8f
//    )

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

        // 开始标识位
        state.isRunning = true

        onSwapContent()
        awaitFrame()

        state.animation.animateTo(1f,pushAnimation)
        onAnimatedFinished?.let { it() }
        // 结束标志位
        LogUtil.debug("state.isRunning=${state.isRunning}")
        state.isRunning = false
        LogUtil.debug("state.isRunning=${state.isRunning}")
    }

    private suspend fun internalPop(
        key: Any,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwapContent: suspend () -> Unit
    ) {
        val state = states[key] ?: return

        // 开始标识位
        state.isRunning = true

        onSwapContent()
        awaitFrame()

        state.animation.animateTo(0f,popAnimation)

        onAnimatedFinished?.let { it() }
        // 结束标志位
        state.isRunning = false
    }
}