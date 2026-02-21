package com.xah.container.ui.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.xah.common.util.ScreenCornerHelper
import com.xah.container.logic.AdaptiveBezierRectInterpolator
import com.xah.container.logic.SharedContainerRegistry
import com.xah.container.ui.util.LocalSharedContainerRegistry


@Composable
fun SharedContainerRoot(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(Unit) {
        ScreenCornerHelper(view)
    }

    val registry = remember { SharedContainerRegistry(scope) }

    val screenHeightPx = with(LocalDensity.current) {
        LocalConfiguration.current.screenHeightDp.dp.toPx()
    }
    registry.rectInterpolator = AdaptiveBezierRectInterpolator(screenHeightPx)

    CompositionLocalProvider(
        LocalSharedContainerRegistry provides registry
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