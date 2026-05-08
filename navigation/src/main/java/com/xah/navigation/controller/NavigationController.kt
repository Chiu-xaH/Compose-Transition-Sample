package com.xah.navigation.controller

import android.os.Build
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import com.sharednav.common.util.LogUtil
import com.sharednav.common.util.PredictiveUtil
import com.xah.container.controller.SharedRegistry
import com.xah.container.model.SharedContainerState
import com.xah.navigation.anim.effect.DefaultTransitionEffect
import com.xah.navigation.model.action.ActionType
import com.xah.navigation.model.action.LaunchMode
import com.xah.navigation.model.anim.EffectLevel
import com.xah.navigation.model.anim.Transition
import com.xah.navigation.model.anim.TransitionEffect
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
    val defaultTransitionEffect: TransitionEffect,
    var sharedRegistry : SharedRegistry? = null,
) {
    val stack: List<StackEntry> get() = _stack

    val current by derivedStateOf {
        _stack.last()
    }

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
    var enableKeepAlive by mutableStateOf(false)

    val transitionProgress = Animatable(0f)

    companion object {
        const val DEFAULT_SHARED_MAX_PRECENT = 0.875f
        const val DEFAULT_SHARED_SPEC = 500
        val DEFAULT_EASING = CubicBezierEasing(0.4f, 0.65f, 0.25f, 1.0f)
    }
    val defaultSpecWithTinyScale = tween<Float>(250)
//    private val defaultSpec = tween<Float>(animationSpecSharedTween*13/10)
    private val popAnimationWithShared = tween<Float>(DEFAULT_SHARED_SPEC*7/5)
    private val pushAnimationWithShared = tween<Float>(DEFAULT_SHARED_SPEC)
//    private val popAnimation = tween<Float>(animationSpecSharedTween*6/5, easing = CubicBezierEasing(0.4f, 0.65f, 0.25f, 1.0f))
//    private val pushAnimation = tween<Float>(animationSpecSharedTween*6/5, easing = CubicBezierEasing(0.4f, 0.65f, 0.25f, 1.0f))

    var inPredictive by mutableStateOf(false)

    private fun getAnimation() =
        if (sharedRegistry?.isRunning == true) {
            when(transition!!.type) {
                ActionType.POP -> popAnimationWithShared
                ActionType.PUSH -> pushAnimationWithShared
            }
        } else {
            if(transitionLevel == EffectLevel.NONE) {
                defaultSpecWithTinyScale
            } else {
                val transitionMode = current().transitionMode
                when(transition!!.type) {
                    ActionType.POP -> transitionMode.popAnimation
                    ActionType.PUSH -> transitionMode.pushAnimation
                }
            }
        }

    private fun createAndPush(
        destination : Destination,
        effect : TransitionEffect
    ) {
        val newEntry = StackEntry(
            id = UUID.randomUUID().toString(),
            destination = destination,
            transitionMode = effect
        )
        _stack += newEntry
        historyQueue.add(destination)
    }

    private fun removeAndPop() : StackEntry? {
        if(canPop()) {
            return _stack.removeAt(_stack.size-1)
        }
        return null
    }


    private fun pushInternal(
        destination: Destination,
        launchMode: LaunchMode,
        effect: TransitionEffect
    ) {
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            var cachedEntry : StackEntry? = null
            // 并行动画
            if(isTransitioning && transition?.type == ActionType.POP) {
                val reuse =
                    (launchMode is LaunchMode.Push && launchMode.reuse) ||
                    (launchMode is LaunchMode.Single && launchMode.reuse) ||
                    (launchMode is LaunchMode.PopToExisting && launchMode.actionType == ActionType.PUSH)

                if(reuse && isCurrentDestination(destination)) {
                    // 同一界面的打断，无需解除容器共享和重建栈
                    cachedEntry = removeAndPop()
                } else {
                    sharedRegistry?.cancelPop()
                    removeAndPop()
                }
            }

            val from = current()

            when (launchMode) {
                is LaunchMode.Push -> {
                    if(launchMode.reuse) {
                        // 如果栈顶是目标项目，则复用
                        if (isCurrentDestination(destination)) {
                            LogUtil.debug("Push(reuse=true) : current is target destination ${destination.key}")
                            // 如果栈顶就是目标，保持栈顶不变
                            return@launch
                        } else {
                            if(cachedEntry != null) {
                                LogUtil.debug("Push(reuse=true) : reuse destination ${destination.key}")
                                _stack.add(cachedEntry)
                            } else {
                                LogUtil.debug("Push(reuse=true) : create destination ${destination.key}")
                                createAndPush(destination,effect)
                            }
                        }
                    } else {
                        LogUtil.debug("Push(reuse=false) : create destination ${destination.key}")
                        // 每次都创建新的并加入栈
                        createAndPush(destination,effect)
                    }
                }
                is LaunchMode.Single -> {
                    if(launchMode.reuse) {
                        // 栈内存在则复用并清空其余项，没有则直接CLEAR_STACK
                        // 从栈底（索引0）开始寻找
                        val existingIndex = _stack.indexOfFirst { it.destination == destination }
                        if (existingIndex != -1) {
                            LogUtil.debug("Single(reuse=true) : found destination ${destination.key}")
                            // 目标项已经存在，复用
                            val item = _stack[existingIndex]
                            _stack.clear()
                            _stack.add(item)
                        } else {
                            LogUtil.debug("Single(reuse=true) : not found destination ${destination.key}")
                            // 如果栈中没有该目标，直接清空栈并压入
                            createAndPushClearly(destination,effect)
                        }
                    } else {
                        LogUtil.debug("Single(reuse=false) : create destination ${destination.key}")
                        // 清空栈并压入
                        createAndPushClearly(destination,effect)
                    }
                }
                is LaunchMode.PopToExisting -> {
                    // 如果栈顶就是目标，保持栈顶不变
                    if(isCurrentDestination(destination)) {
                        LogUtil.debug("PopToExisting : current is target destination ${destination.key}")
                        return@launch
                    }
                    // 如果栈中已经有该目标，则清除其之上的所有栈并复用它
                    if(previous()?.destination == destination) {
                        // 等效于POP
                        LogUtil.debug("PopToExisting : equal Pop ${destination.key}")
                        pop()
                        return@launch
                    }
                    val existingIndex = _stack.indexOfFirst { it.destination == destination }
                    if (existingIndex != -1) {
                        LogUtil.debug("PopToExisting : found destination ${destination.key}")
                        _stack.subList(existingIndex + 1, _stack.size-1).clear() // 清除中间元素
                        popInternal()
                        return@launch
                    } else {
                        LogUtil.debug("PopToExisting : not found destination ${destination.key}")
                        launchMode.actionType = ActionType.PUSH
                        createAndPush(destination,effect)
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
                to = current(),
                effect = effect
            )
        }
    }

    private fun popInternal() {
        scope.launch(start = CoroutineStart.UNDISPATCHED) {

            if (!canPop()) return@launch

            val from = current()
            val to = previous() ?: return@launch

            val type = ActionType.POP

            snap(type)

            transition = Transition(
                type = type,
                from = from,
                to = to,
                effect = from.transitionMode
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

    fun animate() {
        scope.launch {
            internalAnimate()
        }
    }


    private suspend fun internalAnimate() {
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
        transitionProgress.animateTo(targetValue = target, animationSpec = getAnimation())

        // 移除栈，置状态
        if (transition?.type == ActionType.POP) {
            removeAndPop()
        }
        transition = null
        isTransitioning = false
    }

    private fun canShared() = transitionLevel != EffectLevel.NONE && sharedRegistry != null

    fun push(
        destination: Destination,
        launchMode: LaunchMode = LaunchMode.Push(reuse = true),
        effect: TransitionEffect = defaultTransitionEffect,
    ) {
        if(
            canShared() &&
            launchMode.actionType == ActionType.PUSH &&
            sharedRegistry!!.canPush(destination.key)
        ) {
            sharedRegistry!!.push(
                destination.key,
                onAnimatedFinished = { awaitTransition() },
            ) {
                pushInternal(destination,launchMode,defaultTransitionEffect)
            }
        } else {
            pushInternal(destination,launchMode,effect)
        }
    }

    fun pop() {
        if(
            canShared() &&
            sharedRegistry!!.canPop()
        ) {
            sharedRegistry!!.pop(
                current().destination.key,
                onAnimatedFinished = { awaitTransition() }
            ) {
                popInternal()
            }
        } else {
            popInternal()
        }
    }

    suspend fun awaitTransition() = snapshotFlow { isTransitioning }.filter { !it }.first()

    fun current() : StackEntry = _stack.last()

    fun isCurrentDestination(destination : Destination) : Boolean = current().destination == destination

    fun previous() : StackEntry? = _stack.getOrNull(_stack.lastIndex - 1)

    fun canPop() : Boolean = _stack.size > 1


    // 清空栈并压入
    private fun createAndPushClearly(
        destination : Destination,
        effect: TransitionEffect
    ) {
        _stack.clear()
        createAndPush(destination,effect)
    }

    suspend fun startPredictiveBackShared() : SharedContainerState? {
        if(canShared()) {
            return sharedRegistry!!.startPredictiveBack(
                current().destination.key,
            ) {
                startPredictiveBack()
            }
        } else {
            startPredictiveBack()
            return null
        }
    }

    fun startPredictiveBackSharedAsync(
        onResult: (SharedContainerState?) -> Unit
    ) {
        scope.launch {
            val result = startPredictiveBackShared()
            onResult(result)
        }
    }

    private fun startPredictiveBack() {
        scope.launch {
            if (!canPop()) return@launch
            val from = current()
            val to = previous() ?: return@launch
            snap(ActionType.POP)
            transition = Transition(
                type = ActionType.POP,
                from = from,
                to = to,
                effect = from.transitionMode
            )
            inPredictive = true
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
            val canShared = state != null && canShared()
            val easedContainer = getPredictiveMaxValue(progress)

            if (canShared) {
                launch {
                    sharedRegistry!!.updatePredictiveBack(easedContainer, dampOffset(offset), state)
                }
            }
            launch {
                updatePredictiveBack(
                    // 有容器的时候背景不动
                    if(canShared) 1f else easedContainer
                )
            }
        }
    }

    fun getPredictiveMaxValue(
        progress : Float
    ) : Float {
        val minValue = if(transitionLevel == EffectLevel.NONE) 0.6f else current().transitionMode.predictiveMinValue
        val easedContainer = 1f - ((1f - minValue) * progress.pow(0.5f))
        return easedContainer
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
            inPredictive = false
            transitionProgress.animateTo(0f, getAnimation())
            removeAndPop()
            transition = null
            isTransitioning = false
        }
    }

    fun confirmPredictiveBackShared(
        state : SharedContainerState?
    ) {
        val canShared = state != null && canShared()
        scope.launch {
            if (canShared) {
                launch {
                    sharedRegistry!!.confirmPredictiveBack(state) {
                        awaitTransition()
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
            inPredictive = false
        }
    }

    fun cancelPredictiveBackShared(
        state : SharedContainerState?
    ) {
        val canShared = state != null && canShared()
        scope.launch {
            if (canShared) {
                launch {
                    sharedRegistry!!.cancelPredictiveBack(state) {
                        awaitTransition()
                    }
                }
            }
            launch { cancelPredictiveBack() }
        }
    }


    init {
        if(_stack.isEmpty()) {
            createAndPush(startDestination,defaultTransitionEffect)
        }
    }
}