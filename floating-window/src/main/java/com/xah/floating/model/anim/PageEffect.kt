package com.xah.floating.model.anim

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
@Immutable
data class PageEffect(
    val scale: Float,
    val blur: Dp,
    val mask: Float,
) {
    fun lerp(progress : Float): PageEffect {
        val end = NonePageEffect
        return PageEffect(
            scale = lerp(scale, end.scale, progress),
            blur = lerp(blur, end.blur, progress),
            mask = lerp(mask, end.mask, progress),
        )
    }
}

private val NonePageEffect = PageEffect(
    scale = 1f,
    blur = 0.dp,
    mask = 0f
)

