package com.xah.container.component.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sharednav.common.util.LogUtil
import com.xah.container.controller.SharedRegistry
import com.xah.container.controller.SharedRegistryViewModel
import com.xah.container.util.LocalSharedRegistry
import com.xah.container.util.LocalSharedRegistrySafely

@Composable
fun rememberSharedRegistry() : SharedRegistry {
    val scope = rememberCoroutineScope()
    val vm: SharedRegistryViewModel = viewModel(factory = SharedRegistryViewModel.Factory())
    return remember { SharedRegistry(scope,vm.states) }
}

@Composable
expect fun ScreenCornerInit()

@Composable
fun SharedContainerRoot(
    content: @Composable () -> Unit
) {
    val registry = rememberSharedRegistry()

    ScreenCornerInit()

    LaunchedEffect(registry.enabled) {
        if(!registry.enabled) {
            // 注销所有state
            registry.clearStates()
        }
    }
    val density = LocalDensity.current

    CompositionLocalProvider(
        LocalSharedRegistrySafely provides registry,
        LocalSharedRegistry provides registry
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { (width, height) ->
                    registry.screenRect = with(density) {
                        Rect(0f, 0f, width.toFloat(), height.toFloat())
                    }
                }
        ) {
            // 界面
            content()
            // Overlay 永远在界面下面
            SharedContainerOverlay()
        }
    }
}