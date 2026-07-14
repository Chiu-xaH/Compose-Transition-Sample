package com.xah.transition.ui.util

import androidx.compose.runtime.Composable
import com.xah.navigation.util.PlatformContext
import com.xah.transition.util.PlatformActivity
import com.xah.transition.util.PlatformView

@Composable
actual fun LocalPlatformActivity(): PlatformActivity = PlatformActivity()

@Composable
actual fun LocalPlatformContext(): PlatformContext = PlatformContext()

@Composable
actual fun LocalPlatformView(): PlatformView = PlatformView()