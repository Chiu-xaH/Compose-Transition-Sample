package com.xah.container

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Controller for interactive (gesture-driven) shared-container transitions.
 *
 * This module stays decoupled from navigation: navigation (or any state machine) can call these
 * methods to drive progress (0..1), and decide commit/cancel timing.
 */
interface SharedContainerController {
    /** Prepare the transition for [key] (optional for non-interactive usage). */
    fun prepare(key: Any)

    /** Start an interactive session (typically when gesture begins). */
    fun begin(key: Any)

    /** Update interactive progress in [0f, 1f]. */
    fun updateProgress(key: Any, progress: Float)

    /** Finish to 1f and complete the transition. */
    fun commit(key: Any)

    /** Animate back to 0f and cancel the transition. */
    fun cancel(key: Any)
}

val LocalSharedContainerController = staticCompositionLocalOf<SharedContainerController?> { null }

