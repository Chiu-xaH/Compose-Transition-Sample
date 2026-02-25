package com.xah.navigation.model

import androidx.compose.runtime.Composable

abstract class Destination {
    abstract val key: String
    abstract val title : String
    val description : String? = null
    val icon : Int? = null
    // ...可扩展

    @Composable
    abstract fun Content()
}