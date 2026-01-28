package com.xah.transition.ui.model

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.unit.lerp

@Immutable
data class UnderPageVisualEffect(
    val scale: Float,
    val blur: Dp,
    val dim: Float
) {
    companion object {
        val None = UnderPageVisualEffect(
            scale = 1f,
            blur = 0.dp,
            dim = 0f
        )
    }
}

@Stable
class PredictiveEffectScope(
    val progress: Float
) {
    // 背景压暗程度（0f ~ 1f）
    val dimAlpha: Float
        get() = lerp(0f, 0.35f, progress)

    // 背景缩放
    val scale: Float
        get() = lerp(1f, 0.92f, progress)

    // 背景模糊
    val blur: Dp
        get() = lerp(0.dp, 16.dp, progress)
}
