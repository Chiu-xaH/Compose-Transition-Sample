package com.xah.transition.ui.util

import androidx.compose.runtime.Composable
import com.sharednav.common.kmp.PlatformContext
import com.xah.transition.util.PlatformActivity
import com.xah.transition.util.PlatformView

@Composable
expect fun LocalPlatformActivity() : PlatformActivity

@Composable
expect fun LocalPlatformContext() : PlatformContext

@Composable
expect fun LocalPlatformView() : PlatformView