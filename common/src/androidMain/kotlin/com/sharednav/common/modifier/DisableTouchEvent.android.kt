package com.sharednav.common.modifier

import androidx.compose.ui.Modifier
import android.view.MotionEvent
import androidx.compose.ui.input.pointer.pointerInteropFilter

actual fun Modifier.disableTouchEvent(intercept : Boolean,onDisabledClick : (() -> Unit)?) = this.pointerInteropFilter { event ->
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