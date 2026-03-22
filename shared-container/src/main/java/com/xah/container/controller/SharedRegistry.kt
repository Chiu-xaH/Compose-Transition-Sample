package com.xah.container.controller

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sharednav.common.LogUtil
import com.xah.container.anim.LinearRectInterpolator
import com.xah.container.anim.RectInterpolator
import com.xah.container.model.ContainerFilledStrategy
import com.xah.container.model.SharedContainerState
import com.xah.container.model.State
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch

class SharedRegistry(
    private val scope: CoroutineScope,
) {
    val states = mutableStateMapOf<String, SharedContainerState>()
    val runningStates: List<SharedContainerState> by derivedStateOf {
        states.values.filter { it.isRunning() }
    }
    val isRunning: Boolean by derivedStateOf {
        states.values.any { it.isRunning() }
    }
    val isWaitingFrame: Boolean by derivedStateOf {
        states.values.any { it.currentState == State.MEASURING_CONTAINER || it.currentState == State.MEASURING_CONTENT }
    }

    var needWaitMultiFrame : Boolean = true

    private fun SharedContainerState.isRunning() = currentState == State.TRANSITING && containerRect != null && contentRect != null

    var enabled by mutableStateOf(true)

    var animationTime by mutableIntStateOf(500)

    /**
     * 最大等帧时长，为什么需要等，本质上取决于导航栈的设计，如果导航栈只能保持一个页面，其余页面都被销毁，当POP时需要等下面的初始化完成，才能记录容器位置，如果栈中内容都不销毁，那么就只需要等1帧（16ms）
     * 如果waitFrameMaxValue=0或者enableWaitFrameMaxValue=false，则等1帧
     * 界面越复杂，性能越差，需要等的帧越大，但是一般在10帧以内
     */
    var waitFrameMaxValue by mutableIntStateOf(10)

    var pushX1 by mutableFloatStateOf(0.4f)
    var pushY1 by mutableFloatStateOf(0.65f)
    var pushX2 by mutableFloatStateOf(0.25f)
    var pushY2 by mutableFloatStateOf(1.0f)

    var popX1 by mutableFloatStateOf(0.4f)
    var popY1 by mutableFloatStateOf(0.65f)
    var popX2 by mutableFloatStateOf(0.15f)
    var popY2 by mutableFloatStateOf(1.0f)

    fun getPushAnimation() = tween<Float>(animationTime, easing = CubicBezierEasing(pushX1,pushY1,pushX2,pushY2))
    fun getPopAnimation() = tween<Float>(animationTime, easing = CubicBezierEasing(popX1,popY1,popX2,popY2))
//    private val popAnimation = tween<Float>(animationTime.toInt(), easing = CubicBezierEasing(0.4f, 0.65f, 0.15f, 1.0f))
//    private val pushAnimation = tween<Float>(animationTime.toInt(), easing = CubicBezierEasing(0.4f, 0.65f, 0.25f, 1.0f))

    var rectInterpolator: RectInterpolator = LinearRectInterpolator

    // 渐隐、圆角变化比容器变化时长
    val speedUpRadio = 1.5f

    // 单边填充or双边填充
    var extensionDouble by mutableStateOf(false)

    var enableShader by mutableStateOf(ContainerFilledStrategy.CAN_USE_SHADER)

    fun register(
        key: String,
    ): SharedContainerState = states.getOrPut(key) {
        SharedContainerState(key)
    }

    fun unregister(state : SharedContainerState) = unregister(state.key)

    fun unregister(key: String) = states.remove(key)

    fun get(
        key: String,
    ): SharedContainerState? {
        return states[key]
    }

    fun push(
        key: String,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwap: suspend () -> Unit
    ) {
        scope.launch {
            internalPush(key,onAnimatedFinished, onSwap)
        }
    }

    fun pop(
        key: String,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwap: suspend () -> Unit
    ) {
        scope.launch {
            internalPop(key, onAnimatedFinished,onSwap)
        }
    }

    fun push(
        state: SharedContainerState,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwap: suspend () -> Unit
    ) {
        scope.launch {
            internalPush(state,onAnimatedFinished, onSwap)
        }
    }

    fun pop(
        state: SharedContainerState,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwap: suspend () -> Unit
    ) {
        scope.launch {
            internalPop(state, onAnimatedFinished,onSwap)
        }
    }

    private suspend fun internalPush(
        key: String,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwap: suspend () -> Unit
    ) {
        val state = states[key]
        if(state == null) {
            onSwap()
            return
        }
        internalPush(state,onAnimatedFinished,onSwap)
    }

    private suspend fun internalPop(
        key: String,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwap: suspend () -> Unit
    ) {
        val state = states[key]
        if(state == null) {
            onSwap()
            return
        }
        internalPop(state,onAnimatedFinished,onSwap)
    }

    private suspend fun internalPush(
        state: SharedContainerState,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwap: suspend () -> Unit
    ) {
        if(!enabled) {
            onSwap()
            state.currentState = State.CONTENT
            return
        }
        if(
            !waitContentFrame(state) {
                onSwap()
            }
        ) {
            return
        }
        snap(state,true)
        // 开始标识位
        state.currentState = State.TRANSITING

        state.animation.animateTo(1f,getPushAnimation())
        onAnimatedFinished?.let { it() }
        if(needWaitMultiFrame(state)) {
            state.containerRect = null
        }
        // 结束标志位
        state.currentState = State.CONTENT
    }

    private suspend fun internalPop(
        state: SharedContainerState,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwap: suspend () -> Unit
    ) {
        if(!enabled) {
            onSwap()
            state.currentState = State.CONTAINER
            return
        }
        if(
            !waitContainerFrame(state) {
                onSwap()
            }
        ) {
            return
        }
        snap(state,false)
        // 开始标识位
        state.currentState = State.TRANSITING

        state.animation.animateTo(0f,getPopAnimation())

        onAnimatedFinished?.let { it() }
        if(needWaitMultiFrame(state)) {
            state.contentRect = null
        }
        // 结束标志位
        state.currentState = State.CONTAINER
    }


    private suspend fun snap(
        state: SharedContainerState,
        isPush : Boolean
    ) {
        if(state.currentState != State.TRANSITING) {
            state.animation.snapTo(
                if(isPush) {
                    0f
                } else {
                    1f
                }
            )
        }
    }

    private fun needWaitMultiFrame(state: SharedContainerState): Boolean {
        return if(needWaitMultiFrame) {
            state.containerFilledStrategy.getFinalStrategy(enableShader) is ContainerFilledStrategy.Color
        } else {
            false
        }
    }

    private suspend fun waitContainerFrame(
        state: SharedContainerState,
        onSwap: suspend () -> Unit,
    ): Boolean = waitFrame(state,true,onSwap)

    private suspend fun waitContentFrame(
        state: SharedContainerState,
        onSwap: suspend () -> Unit,
    ): Boolean = waitFrame(state,false,onSwap)

    /**
     * 返回时，有些容器是动态加载的，等一个动画时长，如果超时了按导航默认动画走（超时请优化写法）
     */
    private suspend fun waitFrame(
        state: SharedContainerState,
        isContainer : Boolean,
        onSwap: suspend () -> Unit,
    ): Boolean {
        if(waitFrameMaxValue <= 0 || !needWaitMultiFrame(state)) {
            // 等一帧即可
            onSwap()
            // state.isTransiting = true 用于稳住导航不要动，开始测量rect
            // state.isTransiting=true代表此时在打断动画中，rect都不为空，无需再次记录Frame标志
            // state.isTransiting=true时，两个rect一定不为空
            when(state.currentState) {
                State.CONTENT -> {
                    state.currentState = State.MEASURING_CONTAINER
                }
                State.CONTAINER -> {
                    state.currentState = State.MEASURING_CONTENT
                }
                else -> {}
            }
            awaitFrame()
            val rect = if(isContainer) {
                state.containerRect
            } else {
                state.contentRect
            }
            if (rect != null) {
                return true
            } else {
                unregister(state)
                LogUtil.debug("rendering timeout within 1 frame")
                return false
            }
        }

        var frameCount = 0
        // 一定要确保页面切换(onSwap)之后马上等帧(awaitFrame)
        onSwap()
        while (true) {
            // state.isTransiting = true 用于稳住导航不要动，开始测量rect
            // state.isTransiting=true代表此时在打断动画中，rect都不为空，无需再次记录Frame标志
            // state.isTransiting=true时，两个rect一定不为空
            when(state.currentState) {
                State.CONTENT -> {
                    state.currentState = State.MEASURING_CONTAINER
                }
                State.CONTAINER -> {
                    state.currentState = State.MEASURING_CONTENT
                }
                else -> {}
            }
            awaitFrame()
            frameCount++
            val rect = if(isContainer) {
                state.containerRect
            } else {
                state.contentRect
            }
            if (rect != null) {
                return true
            }
            if (frameCount >= waitFrameMaxValue) {
                unregister(state)
                LogUtil.warn("rendering timeout after $frameCount frame")
                return false
            }
        }
    }
}