package com.xah.container.component.overlay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.xah.container.anim.LinearRectInterpolator
import com.xah.container.controller.SharedRegistry

// 窗口不需要圆角，永远都是直角
@Composable
actual fun ScreenCornerInit() = Unit
