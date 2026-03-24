package com.xah.floating.controller

import android.os.Build
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.xah.floating.anim.DefaultEffects
import com.xah.floating.anim.PageEffects
import com.xah.floating.model.FloatingEntry
import com.xah.floating.model.FloatingWindow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.launch
import java.util.UUID

class FloatingController(
    private val scope: CoroutineScope,
    private val _stack: SnapshotStateList<FloatingEntry>,
    val effect : PageEffects = DefaultEffects,
) {
    val stack: List<FloatingEntry> get() = _stack

    var enableBlur by mutableStateOf(Build.VERSION.SDK_INT >= 31)
    var enableShader by mutableStateOf(Build.VERSION.SDK_INT >= 33)

    fun push(window: FloatingWindow) {
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

    fun pop() {
        scope.launch(start = CoroutineStart.UNDISPATCHED) {
            val item = _stack.removeAt(_stack.size-1)
            item.window.onDismissed()
        }
    }

    val isRunning: Boolean by derivedStateOf { _stack.isNotEmpty() }

    fun current(): FloatingEntry? = _stack.lastOrNull()
}
