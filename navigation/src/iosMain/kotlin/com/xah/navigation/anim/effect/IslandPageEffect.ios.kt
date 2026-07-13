package com.xah.navigation.anim.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.TransformOrigin
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.navigation.anim.effect.IslandPageEffects
import com.xah.navigation.model.anim.effect.PageEffects
import com.xah.navigation.model.anim.effect.sub.Rotation
import kotlin.invoke

@Composable
actual fun rememberIslandPageEffects(
    position: TransformOrigin,
    rotation: Rotation
): PageEffects = IslandPageEffects(position,rotation)