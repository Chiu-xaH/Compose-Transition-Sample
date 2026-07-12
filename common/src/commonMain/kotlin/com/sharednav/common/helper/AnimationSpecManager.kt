package com.sharednav.common.helper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import kotlin.math.roundToInt

/**
 * 管理整个库的动画速率
 */
object AnimationSpecManager {
    /**
     * 全局动画速率
     */
    var speedRadio by mutableFloatStateOf(1f)
    const val DEFAULT_SHARED_SPEC = 475

    fun getSharedTween() = getTween(DEFAULT_SHARED_SPEC)
    fun getTween(tween : Int) = (speedRadio * tween).roundToInt()
}