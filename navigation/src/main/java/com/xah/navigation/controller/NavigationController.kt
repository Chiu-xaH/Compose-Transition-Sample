package com.xah.navigation.controller

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.xah.navigation.anim.EffectLevel
import com.xah.navigation.anim.NavTransition
import com.xah.navigation.model.ActionType
import com.xah.navigation.model.Destination
import com.xah.navigation.model.LaunchMode
import com.xah.navigation.model.StackEntry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID


class NavigationController(
    private val scope: CoroutineScope,
    val startDestination: Destination,
) {
    private val _stack = mutableStateListOf<StackEntry>()
    val stack: List<StackEntry> get() = _stack

    var navTransition by mutableStateOf<NavTransition?>(null)
        private set

    var isTransitioning by mutableStateOf(false)
        private set

    var transitionLevel by mutableStateOf(EffectLevel.FULL)

    val transitionProgress = Animatable(0f)

    private val animationSpecSharedTween = 500
    val defaultSpecWithShared = tween<Float>(animationSpecSharedTween*8/5)
    val defaultSpec = tween<Float>(animationSpecSharedTween*13/10)

    fun push(
        destination: Destination,
        launchMode: LaunchMode = LaunchMode.STANDARD
    ) {
        val from = _stack.last()

        when (launchMode) {
            LaunchMode.STANDARD -> {
                // 默认模式，每次都创建新的 Entry 并加入栈
                val newEntry = StackEntry(
                    id = UUID.randomUUID().toString(),
                    destination = destination
                )
                _stack += newEntry
            }
            LaunchMode.SINGLE_TOP -> {
                // 如果栈顶是目标 Activity，则复用栈顶实例
                if (_stack.isNotEmpty() && _stack.last().destination == destination) {
                    // 如果栈顶就是目标，保持栈顶不变
                    return
                } else {
                    val newEntry = StackEntry(
                        id = UUID.randomUUID().toString(),
                        destination = destination
                    )
                    _stack += newEntry
                }
            }
            LaunchMode.SINGLE_TASK -> {
                // 如果栈中已经有该目标 Activity，则清除该 Activity 之上的所有 Activity，并复用它
                val existingIndex = _stack.indexOfFirst { it.destination == destination }
                if (existingIndex != -1) {
                    _stack.subList(existingIndex + 1, _stack.size).clear() // 清除目标 Activity 之上的所有元素
                }
                val newEntry = StackEntry(
                    id = UUID.randomUUID().toString(),
                    destination = destination
                )
                _stack += newEntry
            }
        }

        // 添加过渡动画
        navTransition = NavTransition(
            type = ActionType.PUSH,
            from = from,
            to = _stack.last()
        )
    }

    fun pop() {
        if (_stack.size <= 1) return

        val from = _stack.last()
        val to = _stack[_stack.lastIndex - 1]

        navTransition = NavTransition(
            type = ActionType.POP,
            from = from,
            to = to,
        )
    }

    /**
     * 回到startDestination（栈中有则复用，无则清空栈再push）
     */
    fun home() {
        val from = _stack.last()
        // 从栈底（索引0）开始寻找 startDestination
        var found = false
        for (i in _stack.indices) {
            if (_stack[i].destination == startDestination) {
                // 如果找到了startDestination，清空栈并只保留 startDestination
                _stack.subList(i + 1, _stack.size).clear()
                found = true
                break
            }
        }

        if (!found) {
            // 栈中没有 startDestination，清空栈并 push startDestination
            _stack.clear()
            addHome()
        }
        // 添加过渡动画
        navTransition = NavTransition(
            type = ActionType.POP,
            from = from,
            to = _stack.last()
        )
    }

    fun animate(
        animationSpec: AnimationSpec<Float> = defaultSpec
    ) {
        scope.launch {
            internalAnimate(animationSpec)
        }
    }

    private suspend fun internalAnimate(
        animationSpec: AnimationSpec<Float> = defaultSpec
    ) {
        navTransition ?: return
        // 动画未进行时归位，不影响打断动画
        if(!isTransitioning) {
            transitionProgress.snapTo(when (navTransition!!.type) {
                ActionType.PUSH -> 0f
                ActionType.POP -> 1f
            })
        }

        val target = when (navTransition!!.type) {
            ActionType.PUSH -> 1f
            ActionType.POP -> 0f
        }

        isTransitioning = true
        transitionProgress.animateTo(targetValue = target, animationSpec = animationSpec)

        onTransitionFinished()
        isTransitioning = false
    }

    fun onTransitionFinished() {
        when (navTransition?.type) {
            ActionType.PUSH -> Unit
            ActionType.POP -> {
                if(_stack.size > 1) {
                    _stack.removeAt(_stack.size-1)
                }
            }
            null -> Unit
        }
        navTransition = null
    }

    private fun addHome() {
        val rootEntry = StackEntry(
            id = UUID.randomUUID().toString(),
            destination = startDestination
        )
        _stack += rootEntry
    }

    init {
        addHome()
    }
}