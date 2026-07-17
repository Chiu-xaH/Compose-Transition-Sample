package com.xah.navigation.anim.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalView
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.navigation.model.anim.effect.PageEffects
import com.xah.navigation.model.anim.effect.sub.Rotation

@Composable
actual fun rememberIslandPageEffects(
    position: TransformOrigin,
    rotation: Rotation
): PageEffects {
    val view = LocalView.current
    val corner = ScreenCornerHelper(view).getCornerDp()
    return remember(corner,position,rotation) {
        IslandPageEffects(corner,position,rotation)
    }
}
