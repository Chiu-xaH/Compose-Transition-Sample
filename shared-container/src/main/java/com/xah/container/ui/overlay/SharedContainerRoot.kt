package com.xah.container.ui.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import com.xah.container.logic.SharedContainerRegistry
import com.xah.container.ui.util.LocalSharedContainerRegistry


@Composable
fun SharedContainerRoot(
    content: @Composable () -> Unit
) {
    val registry = remember { SharedContainerRegistry() }
    CompositionLocalProvider(
        LocalSharedContainerRegistry provides registry
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 界面
            content()

            // Overlay 永远在界面下面
            Box(Modifier.zIndex(0f)) {
                SharedContainerOverlay()
            }
        }
    }
}