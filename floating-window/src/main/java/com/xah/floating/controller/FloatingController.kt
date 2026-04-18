package com.xah.floating.controller

import android.os.Build
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import com.xah.container.controller.SharedRegistry
import com.xah.container.model.SharedContainerState
import com.xah.floating.anim.DefaultBackgroundEffect
import com.xah.floating.model.WindowEntry
import com.xah.floating.model.Window
import com.xah.floating.model.anim.BackgroundEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.math.pow

class FloatingController(
    private val scope: CoroutineScope,
    private val _stack: SnapshotStateList<WindowEntry>,
    private val _inOverlay: MutableState<Boolean>,
    val backgroundEffect : BackgroundEffect = DefaultBackgroundEffect,
    var sharedRegistry : SharedRegistry? = null,
) {
    val stack: List<WindowEntry> get() = _stack
    val inOverlay: Boolean get() = _inOverlay.value

    var enableBlur by mutableStateOf(Build.VERSION.SDK_INT >= 31)
    var enableShader by mutableStateOf(Build.VERSION.SDK_INT >= 33)

    private val visibleStates = mutableMapOf<String, MutableTransitionState<Boolean>>()

    fun registerVisibleState(id: String, state: MutableTransitionState<Boolean>) {
        visibleStates[id] = state
    }

    fun unregisterVisibleState(id: String) {
        visibleStates.remove(id)
    }

    private fun pushInternal(window: Window) {
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            val entry = WindowEntry(
                id = UUID.randomUUID().toString(),
                window = window,
            )
            // 检查是否栈顶已有相同的window
            if(current()?.window == window) {
                return@launch
            }
            _inOverlay.value = true
            _stack.add(entry)
        }
    }

    private fun popInternal() {
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            val entry = _stack.lastOrNull() ?: return@launch
            // 只有关闭最后一个时才需要把背景还原
            if (_stack.size == 1) {
                _inOverlay.value = false
            }
            val visibleState = visibleStates[entry.id]
            if (visibleState != null) {
                // 触发退场动画，等动画完成后再移除
                visibleState.targetState = false
                snapshotFlow { visibleState.isIdle }
                    .filter { it }
                    .first()
            }
            if (_stack.isNotEmpty()) {
                val item = _stack.removeAt(_stack.size - 1)
                item.window.onDismissed()
            }
        }
    }

    fun push(
        window: Window
    ) {
        val registry = this.sharedRegistry
        val key = window.key
        if(registry == null || key == null) {
            this.pushInternal(window)
        } else {
            registry.push(
                key!!,
            ) {
                this.pushInternal(window)
            }
        }
    }

    fun pop() {
        if(!canPop()) {
            return
        }
        val registry = this.sharedRegistry
        val lastKey = this.stack.last().window.key
        if(registry == null || lastKey == null) {
            this.popInternal()
        } else {
            registry.pop(
                lastKey,
            ) {
                this.popInternal()
            }
        }
    }

    val isRunning: Boolean by derivedStateOf { _stack.isNotEmpty() }

    fun current(): WindowEntry? = _stack.lastOrNull()

    fun canPop() = _stack.isNotEmpty()


    suspend fun startPredictiveBackShared() : SharedContainerState? {
        val registry = sharedRegistry
        return stack.last().window.key?.let { registry?.startPredictiveBack(it) {} }
    }


    fun updatePredictiveBackShared(
        progress: Float,
        offset: Offset,
        state : SharedContainerState?
    ) {
        scope.launch {
            val registry = sharedRegistry
            val noneShared = registry == null || state == null
            val minValue = 0.85f
            val easedContainer = 1f - ((1f - minValue) * progress.pow(0.5f))

            if (!noneShared) {
                launch {
                    registry.updatePredictiveBack(easedContainer, offset, state)
                }
            }
        }
    }

    fun confirmPredictiveBackShared(
        state : SharedContainerState?
    ) {
        scope.launch {
            val registry = sharedRegistry
            if (!(registry == null || state == null)) {
                launch {
                    registry.confirmPredictiveBack(state)
                }
            }
            launch { popInternal() }
        }
    }

    fun cancelPredictiveBackShared(
        state : SharedContainerState?
    ) {
        scope.launch {
            val registry = sharedRegistry
            if (!(registry == null || state == null)) {
                launch {
                    registry.cancelPredictiveBack(state)
                }
            }
        }
    }
}
