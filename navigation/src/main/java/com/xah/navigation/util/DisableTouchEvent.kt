package com.xah.navigation.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter

/**
 * 只能拦截点击事件
 */
fun Modifier.touchEvent(enable : Boolean)  = if(enable) this else this.disableTouchEvent()

/**
 * 只能拦截点击事件
 */
fun Modifier.disableTouchEvent()  = this.pointerInteropFilter { true }
