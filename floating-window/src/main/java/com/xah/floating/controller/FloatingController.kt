package com.xah.floating.controller

import android.os.Build
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.xah.container.controller.SharedRegistry
import com.xah.floating.anim.DefaultEffects
import com.xah.floating.model.FloatingEntry
import com.xah.floating.model.FloatingWindow
import com.xah.floating.model.anim.PageEffects
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class FloatingController(
    private val scope: CoroutineScope,
    private val _stack: SnapshotStateList<FloatingEntry>,
    val effect : PageEffects = DefaultEffects,
    var sharedRegistry : SharedRegistry? = null,
) {
    val stack: List<FloatingEntry> get() = _stack

    var enableBlur by mutableStateOf(Build.VERSION.SDK_INT >= 31)
    var enableShader by mutableStateOf(Build.VERSION.SDK_INT >= 33)

    private fun pushInternal(window: FloatingWindow) {
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            val entry = FloatingEntry(
                id = UUID.randomUUID().toString(),
                window = window,
            )
            // 检查是否栈顶已有相同的window
            if(current()?.window == window) {
                return@launch
            }
            _stack.add(entry)
        }
    }

    private fun popInternal() {
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            val item = _stack.removeAt(_stack.size-1)
            item.window.onDismissed()
        }
    }

    fun push(
        window: FloatingWindow
    ) {
        val registry = this.sharedRegistry
        if(registry == null) {
            this.pushInternal(window)
        } else {
            registry.push(
                window.key,
            ) {
                this.pushInternal(window)
            }
        }
    }

    fun pop() {
        val registry = this.sharedRegistry
        if(registry == null) {
            this.popInternal()
        } else {
            registry.pop(
                this.stack.last().window.key
            ) {
                this.popInternal()
            }
        }
    }

    val isRunning: Boolean by derivedStateOf { _stack.isNotEmpty() }

    fun current(): FloatingEntry? = _stack.lastOrNull()
}
