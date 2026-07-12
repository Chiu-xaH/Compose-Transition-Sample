package com.xah.navigation.anim.effect

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.navigation.model.anim.effect.PageEffects
import kotlin.invoke

@Composable
actual fun rememberPushPageEffects(clip: Boolean): PageEffects = PushPageEffects(clip)