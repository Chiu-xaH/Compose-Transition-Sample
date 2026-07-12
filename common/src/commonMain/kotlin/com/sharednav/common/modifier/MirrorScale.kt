package com.sharednav.common.modifier

import androidx.compose.ui.Modifier

expect fun Modifier.scaleMirror(
    scale: Float,
    enabled : Boolean,
): Modifier


