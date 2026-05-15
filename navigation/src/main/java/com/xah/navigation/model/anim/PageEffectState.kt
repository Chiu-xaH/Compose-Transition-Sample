package com.xah.navigation.model.anim

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.util.lerp
import androidx.compose.ui.unit.lerp
import com.sharednav.common.util.lerp

@Immutable
data class BackgroundPageEffectState(
    val enableMirror : Boolean,
    val backgroundColor : Color? = null,
    override val start: PageEffect,
    override val end: PageEffect,
) : BasePageEffectState(start,end)

@Immutable
data class ForegroundPageEffectState(
    override val start: PageEffect,
    override val end: PageEffect,
) : BasePageEffectState(start,end)

@Immutable
abstract class BasePageEffectState(
    open val start: PageEffect,
    open val end: PageEffect,
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
        ),
        rotate = Rotation(
            x = lerp(start.rotate.x,end.rotate.x,progress),
            y = lerp(start.rotate.y,end.rotate.y,progress),
            z = lerp(start.rotate.z,end.rotate.z,progress),
        )
    )
}