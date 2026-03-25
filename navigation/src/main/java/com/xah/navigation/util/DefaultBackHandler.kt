package com.xah.navigation.util

import androidx.compose.runtime.Composable
import com.xah.floating.component.FloatingBackHandler
import com.xah.navigation.component.NavigationBackHandler

@Composable
fun DefaultBackHandler() {
    // TODO 预测式返回
    NavigationBackHandler()
    FloatingBackHandler()
}

