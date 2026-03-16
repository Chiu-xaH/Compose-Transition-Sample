package com.xah.container.controller

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.sharednav.common.LogUtil
import com.xah.container.anim.LinearRectInterpolator
import com.xah.container.anim.RectInterpolator
import com.xah.container.model.ContainerFilledStrategy
import com.xah.container.model.SharedContainerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.launch

class SharedRegistry(
    private val scope: CoroutineScope,
    var needWaitMultiFrame : Boolean = true
) {
    val states = mutableStateMapOf<String, SharedContainerState>()
    val runningStates: List<SharedContainerState> by derivedStateOf {
        states.values.filter { it.isRunning() }
    }
    val isRunning: Boolean by derivedStateOf {
        states.values.any { it.isRunning() }
    }
    val isWaitingFrame: Boolean by derivedStateOf {
        states.values.any { it.isWaitingFrame }
    }

    private fun SharedContainerState.isRunning() = isTransiting && containerRect != null && contentRect != null

    var enabled by mutableStateOf(true)

    var animationTime by mutableIntStateOf(500)

    /**
     * 最大等帧时长，为什么需要等，本质上取决于导航栈的设计，如果导航栈只能保持一个页面，其余页面都被销毁，当POP时需要等下面的初始化完成，才能记录容器位置，如果栈中内容都不销毁，那么就只需要等1帧（16ms）
     * 如果waitFrameMaxValue=0或者enableWaitFrameMaxValue=false，则等1帧
     */
    var waitFrameMaxValue by mutableIntStateOf(3)

    var pushX1 by mutableFloatStateOf(0.4f)
    var pushY1 by mutableFloatStateOf(0.65f)
    var pushX2 by mutableFloatStateOf(0.25f)
    var pushY2 by mutableFloatStateOf(1.0f)

    var popX1 by mutableFloatStateOf(0.4f)
    var popY1 by mutableFloatStateOf(0.65f)
    var popX2 by mutableFloatStateOf(0.15f)
    var popY2 by mutableFloatStateOf(1.0f)

    private fun getPushAnimation() = tween<Float>(animationTime, easing = CubicBezierEasing(pushX1,pushY1,pushX2,pushY2))
    private fun getPopAnimation() = tween<Float>(animationTime, easing = CubicBezierEasing(popX1,popY1,popX2,popY2))
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
    ): SharedContainerState {
        return states.getOrPut(key) {
            LogUtil.debug("register $key")
            SharedContainerState(key)
        }
    }

    fun unregister(state : SharedContainerState) = unregister(state.key)

    fun unregister(key: String) {
        states.remove(key)
        LogUtil.debug("unregister $key")
    }

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
            if(!enabled) {
                onSwap()
                return@launch
            }
            internalPush(key,onAnimatedFinished, onSwap)
        }
    }

    fun pop(
        key: String,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwap: suspend () -> Unit
    ) {
        scope.launch {
            if(!enabled) {
                onSwap()
                return@launch
            }
            internalPop(key, onAnimatedFinished,onSwap)
        }
    }

    fun push(
        state: SharedContainerState,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwap: suspend () -> Unit
    ) {
        scope.launch {
            if(!enabled) {
                onSwap()
                return@launch
            }
            internalPush(state,onAnimatedFinished, onSwap)
        }
    }

    fun pop(
        state: SharedContainerState,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwap: suspend () -> Unit
    ) {
        scope.launch {
            if(!enabled) {
                onSwap()
                return@launch
            }
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
        if(
            !waitContentFrame(state) { onSwap() }
        ) {
            return
        }
        snap(state,true)
        // 开始标识位
        state.isTransiting = true

        state.animation.animateTo(1f,getPushAnimation())
        onAnimatedFinished?.let { it() }
        if(needWaitMultiFrame(state)) {
            state.containerRect = null
        }
        // 结束标志位
        state.isTransiting = false
    }

    private suspend fun internalPop(
        state: SharedContainerState,
        onAnimatedFinished : (suspend () -> Unit)? = null,
        onSwap: suspend () -> Unit
    ) {
        if(
            !waitContainerFrame(state) { onSwap() }
        ) {
            return
        }
        snap(state,false)
        // 开始标识位
        state.isTransiting = true

        state.animation.animateTo(0f,getPopAnimation())

        onAnimatedFinished?.let { it() }
        if(needWaitMultiFrame(state)) {
            state.contentRect = null
        }
        // 结束标志位
        state.isTransiting = false
    }


    private suspend fun snap(
        state: SharedContainerState,
        isPush : Boolean
    ) {
        if(!state.isTransiting) {
            state.animation.snapTo(
                if(isPush) {
                    0f
                } else {
                    1f
                }
            )
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


    private fun needWaitMultiFrame(state : SharedContainerState) : Boolean {
        return if(needWaitMultiFrame) {
            // 实体背景，必须等帧，否则过渡不自然；其余填充方案不是实体背景，就算不等帧过渡也比较自然
            state.containerFilledStrategy.getFinalStrategy(enableShader) is ContainerFilledStrategy.Color
        } else {
            false
        }
    }

    /**
     * 返回时，有些容器是动态加载的，等一个动画时长，如果超时了按导航默认动画走（超时请优化写法）
     */
    private suspend fun waitFrame(
        state: SharedContainerState,
        isContainer : Boolean,
        onSwap: suspend () -> Unit,
    ): Boolean {
        if(waitFrameMaxValue < 1 || !needWaitMultiFrame(state)) {
            // 等一帧即可
            onSwap()
            // state.isTransiting = true 用于稳住导航不要动，开始测量rect
            if(!state.isTransiting) {
                // state.isTransiting=true代表此时在打断动画中，rect都不为空，无需再次记录Frame标志
                // state.isTransiting=true时，两个rect一定不为空
                state.isWaitingFrame = true
            }
            awaitFrame()
            val rect = if(isContainer) {
                state.containerRect
            } else {
                state.contentRect
            }
            if (rect != null) {
                LogUtil.debug("start transition, waited 1 frame")
                state.isWaitingFrame = false
                return true
            } else {
                unregister(state)
                LogUtil.debug("rendering timeout within 1 frame")
                state.isWaitingFrame = false
                return false
            }
        }

        var frameCount = 0
        // 一定要确保页面切换(onSwap)之后马上等帧(awaitFrame)
        onSwap()
        while (true) {
            // state.isTransiting = true 用于稳住导航不要动，开始测量rect
            if(!state.isTransiting) {
                // state.isTransiting=true代表此时在打断动画中，rect都不为空，无需再次记录Frame标志
                // state.isTransiting=true时，两个rect一定不为空
                state.isWaitingFrame = true
            }
            awaitFrame()
            frameCount++
            val rect = if(isContainer) {
                state.containerRect
            } else {
                state.contentRect
            }
            if (rect != null) {
                LogUtil.debug("start transition, waited $frameCount frame")
                state.isWaitingFrame = false
                return true
            }
            if (frameCount > waitFrameMaxValue) {
                unregister(state)
                LogUtil.warn("rendering timeout within $frameCount frame")
                state.isWaitingFrame = false
                return false
            }
        }
    }
}