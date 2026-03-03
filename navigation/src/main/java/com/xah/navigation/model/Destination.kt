package com.xah.navigation.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.xah.navigation.utils.LocalNavigationDestination

abstract class Destination {
    /**
     * 也作为共享容器Key
     */
    abstract val key: String
    // ...可扩展

    @Composable
    fun Screen() {
        CompositionLocalProvider(
            LocalNavigationDestination provides this
        ) {
            Content()
        }
    }

    @Composable
    abstract fun Content()
}