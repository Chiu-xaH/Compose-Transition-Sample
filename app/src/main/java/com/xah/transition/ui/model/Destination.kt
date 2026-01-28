package com.xah.transition.ui.model

import androidx.compose.runtime.Composable

interface Destination {
    val key: String
    @Composable fun Content()
}
