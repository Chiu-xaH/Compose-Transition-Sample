package com.sharednav.common.modifier

import androidx.compose.ui.Modifier

actual fun Modifier.disableTouchEvent(
    intercept: Boolean,
    onDisabledClick: (() -> Unit)?
) = this