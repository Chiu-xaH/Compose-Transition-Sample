package com.xah.floating.model

import androidx.compose.runtime.Composable

abstract class FloatingWindow {

    abstract val key : String

    open fun onDismissed() {}

    @Composable
    abstract fun Content()
}
