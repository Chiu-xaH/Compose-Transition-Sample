package com.sharednav.common.modifier

import android.view.MotionEvent
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter

/**
 * 只能拦截点击事件
 */
fun Modifier.touchEvent(enable : Boolean,onDisabledClick : (() -> Unit)? = null)  = if(enable) this else this.disableTouchEvent(onDisabledClick)

/**
 * 只能拦截点击事件
 */
fun Modifier.disableTouchEvent(onDisabledClick : (() -> Unit)? = null)  = this.pointerInteropFilter { event ->
    // 点击onDisabledClick
    when (event.action) {
        MotionEvent.ACTION_DOWN -> {
            true
        }
        MotionEvent.ACTION_UP -> {
            onDisabledClick?.invoke()
            true
        }
        else -> true
    }
}
