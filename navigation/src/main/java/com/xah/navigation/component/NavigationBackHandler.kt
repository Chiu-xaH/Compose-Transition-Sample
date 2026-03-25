package com.xah.navigation.component

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import com.xah.navigation.util.LocalNavController

@Composable
fun NavigationBackHandler() {
    val navController = LocalNavController.current
    BackHandler(enabled = navController.stack.size > 1) {
        navController.pop()
    }
}