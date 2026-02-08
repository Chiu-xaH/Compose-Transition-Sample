package com.xah.container

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

/**
 * Animation spec for [SharedContainer].
 *
 * This module is intentionally decoupled from navigation. The integration layer (e.g. your
 * NavHost) controls when both start/end containers appear/disappear; this spec only controls how
 * the overlay animates once both positions are known.
 */
open class SharedContainerTransitionSpec(
    val pathMotionFactory: PathMotionFactory = LinearMotionFactory,
    /**
     * Frames to wait for before starting transition. Useful when the frame skip caused by
     * rendering the new screen makes the animation not smooth.
     */
    val animationSpec : AnimationSpec<Float> = tween(
        durationMillis = 750,
        easing = FastOutSlowInEasing
    ),
    val direction: TransitionDirection = TransitionDirection.Auto,
    val fadeMode: FadeMode = FadeMode.Cross,
    val fadeProgressThresholds: ProgressThresholds? = null
)

val DefaultSharedContainerTransitionSpec = SharedContainerTransitionSpec()

