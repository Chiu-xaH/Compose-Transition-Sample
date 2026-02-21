package com.xah.common

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter


fun Modifier.touchEvent(enable : Boolean)  = if(enable) this else this.disableTouchEvent()

fun Modifier.disableTouchEvent()  = this.pointerInteropFilter { true }