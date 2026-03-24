package com.xah.floating.anim

import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.unit.dp
import com.xah.floating.model.anim.BackgroundEffect
import com.xah.floating.model.anim.ForegroundEffect
import com.xah.floating.model.anim.PageEffect
import com.xah.floating.model.anim.PageEffects

val DefaultPageEffect = PageEffect(
    scale = 1f,
    blur = 0.dp,
    mask = 0.3f
)

val DefaultBackgroundEffect = BackgroundEffect(
    pageEffect = DefaultPageEffect,
    animationSpec = spring()
)

val DefaultForegroundEffect = ForegroundEffect(
    enter = scaleIn(initialScale = 1.1f),
    exit = scaleOut(targetScale = 1.1f)
)

val DefaultEffects = PageEffects(
    backgroundEffect = DefaultBackgroundEffect,
    foregroundEffect = DefaultForegroundEffect
)