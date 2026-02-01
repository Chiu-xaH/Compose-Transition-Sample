package com.xah.container

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.lerp
import androidx.compose.ui.util.lerp as lerpFloat

internal val Rect.area: Float
    get() = width * height

internal fun calculateDirection(start: Rect, end: Rect): TransitionDirection =
    if (end.area > start.area) TransitionDirection.Enter else TransitionDirection.Return

internal fun calculateAlpha(
    direction: TransitionDirection?,
    fadeMode: FadeMode?,
    fraction: Float, // absolute (0..1)
    isStart: Boolean
): Float = when (fadeMode) {
    FadeMode.In, null -> if (isStart) 1f else fraction
    FadeMode.Out -> if (isStart) 1 - fraction else 1f
    FadeMode.Cross -> if (isStart) 1 - fraction else fraction
    FadeMode.Through -> {
        val threshold = if (direction == TransitionDirection.Enter)
            FadeThroughProgressThreshold else 1 - FadeThroughProgressThreshold
        if (fraction < threshold) {
            if (isStart) 1 - fraction / threshold else 0f
        } else {
            if (isStart) 0f else (fraction - threshold) / (1 - threshold)
        }
    }
}

internal fun calculateTopCenter(
    start: Rect,
    end: Rect?,
    fraction: Float, // relative
    pathMotion: PathMotion?
): Offset = if (end == null) start.topCenter else pathMotion!!.invoke(
    start.topCenter,
    end.topCenter,
    fraction
)

internal fun lerpSize(start: Size, end: Size, fraction: Float): Size =
    Size(
        lerpFloat(start.width, end.width, fraction),
        lerpFloat(start.height, end.height, fraction)
    )

