package com.xah.navigation.state

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.staticCompositionLocalOf

// 根导航
val LocalNavStackState = staticCompositionLocalOf<NavStackState> {
    error("未提供根NavStackState")
}

@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = staticCompositionLocalOf<SharedTransitionScope> {
    error("未提供SharedTransitionScope,请确认是否使用了本Library的TransitionNavHost")
}

val LocalAnimatedContentScope = staticCompositionLocalOf<AnimatedContentScope> {
    error("未提供AnimatedContentScope,请确认是否使用了本Library的transitionComposable")
}


