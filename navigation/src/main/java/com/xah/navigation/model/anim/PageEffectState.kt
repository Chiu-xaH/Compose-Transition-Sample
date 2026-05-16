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
    override val effect: PageEffect
) : BasePageEffectState(effect)

@Immutable
data class ForegroundPageEffectState(
    override val effect: PageEffect
) : BasePageEffectState(effect)

@Immutable
abstract class BasePageEffectState(
    open val effect: PageEffect
) {
    fun lerp(progress : Float): PageEffectFrame = with(effect) {
        PageEffectFrame(
            scale = with(scale) {
                lerp(start, end, getFinalProgress(progress))
            },
            blur = with(blur) {
                lerp(start, end, getFinalProgress(progress))
            },
            mask = with(mask) {
                lerp(start, end, getFinalProgress(progress))
            },
            corner = with(corner) {
                lerp(start, end, getFinalProgress(progress))
            },
            alpha = with(alpha) {
                lerp(start, end, getFinalProgress(progress))
            },
            position = with(position) {
                getFinalProgress(progress).let { finalProgress ->
                    TransformOrigin(
                        lerp(start.pivotFractionX,end.pivotFractionX,finalProgress),
                        lerp(start.pivotFractionY,end.pivotFractionY,finalProgress)
                    )
                }
            },
            translationPercent = with(translationPercent) {
                getFinalProgress(progress).let { finalProgress ->
                    Offset(
                        lerp(start.x, end.x, finalProgress),
                        lerp(start.y, end.y, finalProgress)
                    )
                }
            },
            rotate = with(rotate) {
                getFinalProgress(progress).let { finalProgress ->
                    Rotation(
                        x = lerp(start.x,end.x,finalProgress),
                        y = lerp(start.y,end.y,finalProgress),
                        z = lerp(start.z,end.z,finalProgress),
                    )
                }
            }
        )
    }
}