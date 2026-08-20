package com.sharednav.common.modifier

import androidx.compose.ui.Modifier

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.input.pointer.pointerInput

actual fun Modifier.disableTouchEvent(
    intercept: Boolean,
    onDisabledClick: (() -> Unit)?
): Modifier = this.pointerInput(intercept to onDisabledClick) {
    awaitEachGesture {
        val down = awaitFirstDown(requireUnconsumed = false)
        if (intercept) {
            down.consume() // 拦截：消费掉，子组件收不到
        }
        val up = waitForUpOrCancellation()
        if (up != null) {
            if (intercept) up.consume()
            onDisabledClick?.invoke()
        }
    }
}