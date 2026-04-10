package com.sharednav.common.modifier

import android.view.MotionEvent
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter

/**
 * 只能拦截点击事件
 */
fun Modifier.touchEvent(enable : Boolean,intercept : Boolean = true,onDisabledClick : (() -> Unit)? = null)  = if(enable) this else this.disableTouchEvent(intercept,onDisabledClick)

/**
 * 只能拦截点击事件
 */
fun Modifier.disableTouchEvent(intercept : Boolean = true,onDisabledClick : (() -> Unit)? = null)  = this.pointerInteropFilter { event ->
    if(intercept) {
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
    } else {
        if (event.action == MotionEvent.ACTION_UP) {
            onDisabledClick?.invoke()
        }
        false
    }
}