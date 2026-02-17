package com.xah.container.container

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import com.xah.container.animation.DefaultSharedContainerTransitionSpec
import com.xah.container.LocalSharedContainerEnabled
import com.xah.container.animation.SharedContainerTransitionSpec

/**
 * A "shared container" transition primitive.
 *
 * Place [SharedContainer] with the same [key] on both the start and end screens. Wrap everything
 * inside [SharedContainerRoot]. This module does not depend on navigation; navigation (or any
 * state machine) is responsible for keeping both endpoints alive long enough for the overlay
 * transition to run.
 */
@Composable
fun SharedContainer(
    key: Any,
    screenKey: Any,
    modifier: Modifier = Modifier,
    isFullscreen: Boolean = false,
    enabled: Boolean = true,
    color: Color = Color.Transparent,
    cornerRadius: Dp = 0.dp,
    transitionSpec: SharedContainerTransitionSpec = DefaultSharedContainerTransitionSpec,
    onFractionChanged: ((Float) -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val globallyEnabled = LocalSharedContainerEnabled.current
    val effectiveEnabled = enabled && globallyEnabled

    if (!effectiveEnabled) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(cornerRadius))
                .background(color)
        ) {
            content()
        }
        return
    }

    val containerInfo = remember(key, screenKey, color, cornerRadius, transitionSpec, onFractionChanged) {
        SharedContainerInfo(
            key = key,
            screenKey = screenKey,
            color = color,
            cornerRadius = cornerRadius,
            spec = transitionSpec,
            onFractionChanged = onFractionChanged
        )
    }

    val realPlaceholder = placeholder ?: content

    BaseSharedContainer(
        containerInfo = containerInfo,
        isFullscreen = isFullscreen,
        placeholder = realPlaceholder,
        overlay = { SharedContainerOverlayPlaceholder(it) },
        content = { internalModifier ->
            Box(
                modifier = modifier
                    .then(internalModifier)
                    .clip(RoundedCornerShape(cornerRadius))
                    .background(color)
            ) {
                content()
            }
        }
    )
}

