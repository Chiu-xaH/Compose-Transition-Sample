package com.xah.navigation.util

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.compositionLocalOf

fun WindowSizeClass.isLargeScreen(): Boolean = WindowWidthSizeClass.Expanded == this.widthSizeClass

val LocalScreenSize = compositionLocalOf { false }