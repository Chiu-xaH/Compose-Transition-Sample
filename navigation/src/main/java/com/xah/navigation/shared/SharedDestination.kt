package com.xah.navigation.shared

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.xah.container.container.SharedContent
import com.xah.navigation.model.Destination
import com.xah.navigation.utils.LocalNavDestination

abstract class SharedDestination : Destination() {
    @Composable
    override fun Screen() {
        CompositionLocalProvider(
            LocalNavDestination provides this
        ) {
            SharedContent(key) {
                Content()
            }
        }
    }
}