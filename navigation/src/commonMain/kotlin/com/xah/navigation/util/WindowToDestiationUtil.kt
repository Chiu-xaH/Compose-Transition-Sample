package com.xah.navigation.util

import com.xah.floating.controller.FloatingController
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.action.LaunchMode
import com.xah.navigation.model.anim.TransitionEffect
import com.xah.navigation.model.dest.Destination
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(DelicateCoroutinesApi::class)
fun windowToDestination(
    floatingController : FloatingController,
    navController : NavigationController,
    destination: Destination,
    launchMode: LaunchMode = LaunchMode.Push(true),
    effect: TransitionEffect? = null
) {
    // 特殊情况，用全局Scope
    GlobalScope.launch {
        floatingController.pop()
        floatingController.awaitRunning()
        if(effect == null) {
            navController.push(destination,launchMode)
        } else {
            navController.push(destination,launchMode,effect)
        }
    }
}