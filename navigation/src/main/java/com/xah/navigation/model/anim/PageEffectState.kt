package com.xah.navigation.model.anim

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.util.lerp
import androidx.compose.ui.unit.lerp
import com.sharednav.common.util.lerp

@Immutable
data class PageEffectState(
    val enableMirror : Boolean,
    val start: PageEffect,
    val end: PageEffect,
) {
    fun lerp(progress : Float) = PageEffect(
        scale = lerp(start.scale, end.scale, progress),
        blur = lerp(start.blur, end.blur, progress),
        mask = lerp(start.mask, end.mask, progress),
        corner = lerp(start.corner, end.corner, progress),
        alpha = lerp(start.alpha, end.alpha, progress),
        position = TransformOrigin(
            lerp(start.position.pivotFractionX,end.position.pivotFractionX,progress),
            lerp(start.position.pivotFractionY,end.position.pivotFractionY,progress)
        ),
        translationPercent = Offset(
            lerp(start.translationPercent.x,end.translationPercent.x,progress),
            lerp(start.translationPercent.y,end.translationPercent.y,progress)
        )
    )
}