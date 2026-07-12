package com.sharednav.common.util

import androidx.compose.animation.core.tween

object PredictiveUtil {
    fun <T> cancelAnimation() = tween<T>(100)
}