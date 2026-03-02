package com.xah.navigation.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.xah.navigation.utils.LocalNavigationDestination
import java.util.UUID

abstract class Destination {
    /**
     * 也作为共享容器Key
     */
    abstract val key: String
    abstract val title : String
    open val description : String? = null
    open val icon : Int? = null
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