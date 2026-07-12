package com.sharednav.common.modifier

import androidx.compose.ui.Modifier

/**
 * 只能拦截点击事件
 */
fun Modifier.touchEvent(enable : Boolean,intercept : Boolean = true,onDisabledClick : (() -> Unit)? = null)  = if(enable) this else this.disableTouchEvent(intercept,onDisabledClick)

/**
 * 只能拦截点击事件
 */
expect fun Modifier.disableTouchEvent(intercept : Boolean = true,onDisabledClick : (() -> Unit)? = null) : Modifier