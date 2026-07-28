package com.xah.transition.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.core.view.WindowCompat
import com.xah.navigation.registry.DeepLinkRegistry
import com.xah.navigation.util.toDeepLinkUri
import com.xah.transition.ui.screen.AppThemeMain
import com.xah.transition.ui.style.TransparentSystemBars

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            AppThemeMain(
                firstPage = intent?.data?.toDeepLinkUri()?.let { deeplink ->
                    DeepLinkRegistry.parse(deeplink)
                }
            )
            TransparentSystemBars(isSystemInDarkTheme())
        }
    }
}