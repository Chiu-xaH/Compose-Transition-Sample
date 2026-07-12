package com.xah.container.component.overlay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.container.controller.SharedRegistry

@Composable
actual fun ScreenCornerInit() {
    val view = LocalView.current
    LaunchedEffect(view) {
        ScreenCornerHelper(view)
    }
}

@Composable
actual fun QuadraticBezierRectInterpolatorInit(
    registry : SharedRegistry
) {
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenHeightPx = with(density) {
        configuration.screenHeightDp.dp.toPx()
    }
    val screenWidthPx = with(density) {
        configuration.screenWidthDp.dp.toPx()
    }


    LaunchedEffect(
        screenWidthPx,
        screenHeightPx,
        registry.quadraticBezierRectInterpolatorVerticalRadio,
        registry.quadraticBezierRectInterpolatorHorizontalRadio
    ) {
        registry.screenRect = Rect(0f,0f,screenWidthPx,screenHeightPx)
        registry.initQuadraticBezierRectInterpolator()
    }
}