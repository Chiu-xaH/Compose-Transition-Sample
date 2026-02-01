package com.xah.container

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Global (CompositionLocal) switch for enabling [SharedContainer] registration.
 *
 * Useful for integration layers (e.g. a NavHost that keeps an "under" page always composed):
 * you can disable registration on the under page during stable states to avoid re-triggering
 * shared-container transitions, while still rendering the under page for background effects.
 */
val LocalSharedContainerEnabled = staticCompositionLocalOf { true }

