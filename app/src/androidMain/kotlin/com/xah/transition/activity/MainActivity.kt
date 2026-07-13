package com.xah.transition.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import com.xah.navigation.registry.DeepLinkRegistry
import com.xah.navigation.util.toDeepLinkUri
import com.xah.transition.ui.screen.Main
import com.xah.transition.ui.theme.TransitionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            TransitionTheme {
                Main(
                    firstPage = intent?.data?.toDeepLinkUri()?.let { deeplink ->
                        DeepLinkRegistry.parse(deeplink)
                    }
                )
            }
        }
    }
}