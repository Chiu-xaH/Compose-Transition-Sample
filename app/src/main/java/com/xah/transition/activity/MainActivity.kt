package com.xah.transition.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.sharednav.common.util.LogUtil
import com.xah.navigation.registry.DeepLinkRegistry
import com.xah.transition.ui.screen.App
import com.xah.transition.ui.theme.TransitionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TransitionTheme {
                App(
                    firstPage = intent?.data?.let { deeplink ->
                        DeepLinkRegistry.parse(deeplink)
                    }
                )
            }
        }
    }
}