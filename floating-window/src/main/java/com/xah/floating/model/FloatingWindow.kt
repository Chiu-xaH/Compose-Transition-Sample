package com.xah.floating.model

import androidx.compose.runtime.Composable

abstract class FloatingWindow {

    open fun onDismissed() {}

    @Composable
    abstract fun Content()
}
