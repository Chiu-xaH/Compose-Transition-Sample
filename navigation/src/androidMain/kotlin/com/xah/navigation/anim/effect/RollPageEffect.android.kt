package com.xah.navigation.anim.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.navigation.model.anim.effect.PageEffects

@Composable
actual fun rememberRollPageEffects(
    direction: Direction,
    clip: Boolean,
): PageEffects {
    val view = LocalView.current
    val corner = ScreenCornerHelper(view).getCornerDp()
    return remember(corner, direction, clip) {
        RollPageEffects(corner, direction, clip)
    }
}
