package com.xah.transition.ui.util

import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import com.sharednav.common.kmp.PlatformContext
import com.xah.transition.util.PlatformActivity
import com.xah.transition.util.PlatformView

@Composable
actual fun LocalPlatformActivity(): PlatformActivity {
    val activity = LocalActivity.current
    return PlatformActivity(activity)
}

@Composable
actual fun LocalPlatformContext(): PlatformContext {
    val context = LocalContext.current
    return PlatformContext(context)
}

@Composable
actual fun LocalPlatformView(): PlatformView {
    val view = LocalView.current
    return PlatformView(view)
}