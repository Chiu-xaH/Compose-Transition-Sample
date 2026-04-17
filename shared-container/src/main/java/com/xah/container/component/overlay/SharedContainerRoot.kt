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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sharednav.common.helper.ScreenCornerHelper
import com.sharednav.common.util.LogUtil
import com.xah.container.anim.QuadraticBezierRectInterpolator
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
fun SharedContainerRoot(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    LaunchedEffect(view) {
        ScreenCornerHelper(view)
    }

    val registry = rememberSharedRegistry()

    val screenHeightPx = with(density) {
        configuration.screenHeightDp.dp.toPx()
    }
    val screenWidthPx = with(density) {
        configuration.screenWidthDp.dp.toPx()
    }

    LaunchedEffect(screenWidthPx, screenHeightPx) {
        LogUtil.debug("init FullScreenRectInterpolator")
        registry.screenRect = Rect(0f,0f,screenWidthPx,screenHeightPx)
        registry.initFullScreenRectInterpolator(
            QuadraticBezierRectInterpolator(
                screenHeightPx,
                screenWidthPx
            )
        )
    }

    CompositionLocalProvider(
        LocalSharedRegistrySafely provides registry,
        LocalSharedRegistry provides registry
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 界面
            content()
            // Overlay 永远在界面下面
            SharedContainerOverlay()
        }
    }
}