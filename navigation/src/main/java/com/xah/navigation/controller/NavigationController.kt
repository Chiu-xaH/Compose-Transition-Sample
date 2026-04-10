package com.xah.navigation.controller

import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import com.sharednav.common.util.PredictiveUtil
import com.xah.container.controller.SharedRegistry
import com.xah.container.model.SharedContainerState
import com.xah.navigation.model.action.ActionType
import com.xah.navigation.model.action.LaunchMode
import com.xah.navigation.model.anim.EffectLevel
import com.xah.navigation.model.anim.PageEffects
import com.xah.navigation.model.anim.Transition
import com.xah.navigation.model.dest.Destination
import com.xah.navigation.model.dest.StackEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.pow

class NavigationController(
    private val scope: CoroutineScope,
    val startDestination: Destination,
    private val _stack: SnapshotStateList<StackEntry> = mutableStateListOf(),
    val historyQueue: SnapshotStateList<Destination> = mutableStateListOf(),
    val effects: PageEffects,
    var sharedRegistry : SharedRegistry? = null,
) {
    val stack: List<StackEntry> get() = _stack

    var transition by mutableStateOf<Transition?>(null)
        private set

    var isTransitioning by mutableStateOf(false)
        private set

    var transitionLevel by mutableStateOf(EffectLevel.FULL)

    var enableBlur by mutableStateOf(Build.VERSION.SDK_INT >= 31)
    var enableShader by mutableStateOf(Build.VERSION.SDK_INT >= 33)
    var enablePredictiveBack by mutableStateOf(Build.VERSION.SDK_INT >= 33)
    /**
     * 允许Destination.PlaceHolder生效，如果Destination.enforcePlaceHolder为true则不受enableSplashScreen限制
     */
    var enableSplashScreen by mutableStateOf(false)

    /**
     * 是否保留页面真正的不被销毁,这个栈一般是应用的主页面，承载的业务比较多，如果为true页面还在，只不过被盖住了,可节省POP的性能开销（!!!多页面卡顿OOM警告,不建议启用）
     */
    var enableKeepAlive : Boolean = false

    val transitionProgress = Animatable(0f)

    private val animationSpecSharedTween = 500
    val defaultSpecWithTinyScale = tween<Float>(250)
    private val defaultSpec = tween<Float>(animationSpecSharedTween*13/10)
    private val popAnimationWithShared = tween<Float>(animationSpecSharedTween*7/5)
    private val pushAnimationWithShared = tween<Float>(animationSpecSharedTween)
    private val popAnimation = tween<Float>(animationSpecSharedTween*6/5, easing = CubicBezierEasing(0.4f, 0.65f, 0.25f, 1.0f))
    private val pushAnimation = tween<Float>(animationSpecSharedTween*6/5, easing = CubicBezierEasing(0.4f, 0.65f, 0.25f, 1.0f))

    fun getAnimation() =
        if (sharedRegistry?.isRunning == true) {
            when(transition?.type) {
                ActionType.POP -> popAnimationWithShared
                ActionType.PUSH -> pushAnimationWithShared
                else -> defaultSpec
            }
        } else {
            if(transitionLevel == EffectLevel.NONE) {
                defaultSpecWithTinyScale
            } else {
                when(transition?.type) {
                    ActionType.POP -> popAnimation
                    ActionType.PUSH -> pushAnimation
                    else -> defaultSpec
                }
            }
        }

    private fun createAndPush(
        destination : Destination
    ) {
        val newEntry = StackEntry(
            id = UUID.randomUUID().toString(),
            destination = destination
        )
        _stack += newEntry
        historyQueue.add(destination)
    }

    private fun removeAndPop() {
        if(canPop()) {
            _stack.removeAt(_stack.size-1)
        }
    }


    private fun pushInternal(
        destination: Destination,
        launchMode: LaunchMode = LaunchMode.Push(true),
    ) {
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            val from = _stack.last()

            when (launchMode) {
                is LaunchMode.Push -> {
                    if(launchMode.reuse) {
                        // 如果栈顶是目标项目，则复用
                        if (_stack.isNotEmpty() && _stack.last().destination == destination) {
                            // 如果栈顶就是目标，保持栈顶不变
                            return@launch
                        } else {
                            createAndPush(destination)
                        }
                    } else {
                        // 每次都创建新的并加入栈
                        createAndPush(destination)
                    }
                }
                is LaunchMode.Single -> {
                    if(launchMode.reuse) {
                        // 栈内存在则复用并清空其余项，没有则直接CLEAR_STACK
                        // 从栈底（索引0）开始寻找
                        val existingIndex = _stack.indexOfFirst { it.destination == destination }
                        if (existingIndex != -1) {
                            // 目标项已经存在，复用
                            val item = _stack[existingIndex]
                            _stack.clear()
                            _stack.add(item)
                        } else {
                            // 如果栈中没有该目标，直接清空栈并压入
                            _stack.clear()
                            createAndPush(destination)
                        }
                    } else {
                        // 清空栈并压入
                        _stack.clear()
                        createAndPush(destination)
                    }
                }
                is LaunchMode.PopToExisting -> {
                    // 如果栈顶就是目标，保持栈顶不变
                    if(_stack.last().destination == destination) {
                        return@launch
                    }
                    // 如果栈中已经有该目标，则清除其之上的所有栈并复用它
                    val existingIndex = _stack.indexOfFirst { it.destination == destination }
                    if (existingIndex != -1) {
                        _stack.subList(existingIndex + 1, _stack.size-1).clear() // 清除中间元素
                        pop()
                        return@launch
                    } else {
                        launchMode.actionType = ActionType.PUSH
                        createAndPush(destination)
                    }
                }
            }
            // 动画未进行时归位，不影响打断动画
            val type = launchMode.actionType
            snap(type)
            // 添加过渡动画
            transition = Transition(
                type = type,
                from = from,
                to = _stack.last()
            )
        }
    }

    private fun popInternal() {
        scope.launch(start = CoroutineStart.UNDISPATCHED) {

            if (_stack.size <= 1) return@launch

            val from = _stack.last()
            val to = _stack[_stack.lastIndex - 1]

            val type = ActionType.POP

            snap(type)

            transition = Transition(
                type = type,
                from = from,
                to = to,
            )
        }
    }

    // 动画未进行时归位，不影响打断动画
    private suspend fun snap(
        type : ActionType
    ) {
        if(!isTransitioning) {
            transitionProgress.snapTo(
                when(type) {
                    ActionType.PUSH -> 0f
                    ActionType.POP -> 1f
                }
            )
        }
    }

    fun animate(
        animationSpec: AnimationSpec<Float> = getAnimation()
    ) {
        scope.launch {
            internalAnimate(animationSpec)
        }
    }


    private suspend fun internalAnimate(
        animationSpec: AnimationSpec<Float> = defaultSpec
    ) {
        if(sharedRegistry?.isWaitingFrame == true) {
            return
        }
        transition ?: return

        val target = when (transition!!.type) {
            ActionType.PUSH -> 1f
            ActionType.POP -> 0f
        }

        // 设置标志位，开始动画
        isTransitioning = true
        transitionProgress.animateTo(targetValue = target, animationSpec = animationSpec)

        // 移除栈，置状态
        if (transition?.type == ActionType.POP) {
            removeAndPop()
        }
        transition = null
        isTransitioning = false
    }

    fun push(
        destination: Destination,
        launchMode: LaunchMode = LaunchMode.Push(reuse = true),
    ) {
        val registry = this.sharedRegistry
        if(registry == null || launchMode.actionType == ActionType.POP) {
            this.pushInternal(destination,launchMode)
        } else {
            registry.push(
                destination.key,
                onAnimatedFinished = {
                    snapshotFlow { this.isTransitioning }
                        .filter { !it }
                        .first()
                }
            ) {
                this.pushInternal(destination,launchMode)
            }
        }
    }

    fun pop() {
        val registry = this.sharedRegistry
        if(registry == null) {
            this.popInternal()
        } else {
            registry.pop(
                this.stack.last().destination.key,
                onAnimatedFinished = {
                    snapshotFlow { this.isTransitioning }
                        .filter { !it }
                        .first()
                }
            ) {
                this.popInternal()
            }
        }
    }

    fun current() : StackEntry? = _stack.lastOrNull()

    fun canPop() : Boolean = _stack.size > 1

    suspend fun startPredictiveBackShared() : SharedContainerState? {
        val registry = sharedRegistry
        if(registry == null) {
            startPredictiveBack()
            return null
        } else {
            return registry.startPredictiveBack(
                stack.last().destination.key,
            ) {
                startPredictiveBack()
            }
        }
    }

    private fun startPredictiveBack() {
        scope.launch {
            if (_stack.size <= 1) return@launch
            val from = _stack.last()
            val to = _stack[_stack.lastIndex - 1]
            transitionProgress.snapTo(1f)
            transition = Transition(type = ActionType.POP, from = from, to = to)
            isTransitioning = true
        }
    }

    private fun dampOffset(offset: Offset, factor: Float = 0.01f): Offset {
        val distance = offset.getDistance()
        if (distance == 0f) return offset

        val scale = 1f / (1f + distance * factor)
        return offset * scale
    }

    fun updatePredictiveBackShared(
        progress: Float,
        offset: Offset,
        state : SharedContainerState?
    ) {
        scope.launch {
            val registry = sharedRegistry
            val noneShared = registry == null || state == null
            val minValue = if(transitionLevel == EffectLevel.NONE) 0.6f else (
                    effects.backgroundEffect.end.scale - if(noneShared) 0f else 0.0325f
            )
            val easedContainer = 1f - ((1f - minValue) * progress.pow(0.5f))

            if (!noneShared) {
                launch {
                    registry.updatePredictiveBack(easedContainer, dampOffset(offset), state)
                }
            }
            launch {
                updatePredictiveBack(
                    // 有容器的时候背景不动
                    if(noneShared) easedContainer else 1f
                )
            }
        }
    }

    private fun updatePredictiveBack(
        progress: Float,
    ) {
        scope.launch {
            transitionProgress.snapTo(progress)
        }
    }


    private fun confirmPredictiveBack() {
        scope.launch {
            transitionProgress.animateTo(0f, getAnimation())
            removeAndPop()
            transition = null
            isTransitioning = false
        }
    }

    fun confirmPredictiveBackShared(
        state : SharedContainerState?
    ) {
        scope.launch {
            val registry = sharedRegistry
            if (!(registry == null || state == null)) {
                launch {
                    registry.confirmPredictiveBack(state) {
                        snapshotFlow { isTransitioning }
                            .filter { !it }
                            .first()
                    }
                }
            }
            launch { confirmPredictiveBack() }
        }
    }

    private fun cancelPredictiveBack() {
        scope.launch {
            transitionProgress.animateTo(1f, PredictiveUtil.cancelAnimation())
            transition = null
            isTransitioning = false
        }
    }

    fun cancelPredictiveBackShared(
        state : SharedContainerState?
    ) {
        scope.launch {
            val registry = sharedRegistry
            if (!(registry == null || state == null)) {
                launch {
                    registry.cancelPredictiveBack(state) {
                        snapshotFlow { isTransitioning }
                            .filter { !it }
                            .first()
                    }
                }
            }
            launch { cancelPredictiveBack() }
        }
    }


    init {
        if(_stack.isEmpty()) {
            createAndPush(startDestination)
        }
    }
}