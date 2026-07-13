package com.xah.transition.util

import androidx.compose.runtime.Composable

@Composable
expect fun PlatformBackHandler(
    enabled : Boolean = true,
    onBack : () -> Unit
)
