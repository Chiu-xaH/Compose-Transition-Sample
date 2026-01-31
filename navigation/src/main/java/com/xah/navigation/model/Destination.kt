package com.xah.navigation.model

import androidx.compose.runtime.Composable

interface Destination {
    val key: String
    val description : String
    @Composable
    fun Content()
}