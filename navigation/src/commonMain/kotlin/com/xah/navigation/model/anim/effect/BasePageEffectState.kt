package com.xah.navigation.model.anim.effect

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import com.xah.navigation.model.anim.effect.sub.Roll
import com.xah.navigation.model.anim.effect.sub.Rotation

@Immutable
abstract class BasePageEffectState(
    open val effect: PageEffect,
) {
    fun lerp(progress : Float): PageEffectFrame = with(effect) {
        PageEffectFrame(
            scale = with(scale) {
                androidx.compose.ui.util.lerp(start, end, getFinalProgress(progress))
            },
            blur = with(blur) {
                androidx.compose.ui.unit.lerp(start, end, getFinalProgress(progress))
            },
            maskLight = with(maskLight) {
                androidx.compose.ui.graphics.lerp(start, end, getFinalProgress(progress))
            },
            maskDark = with(maskDark) {
                androidx.compose.ui.graphics.lerp(start, end, getFinalProgress(progress))
            },
            corner = with(corner) {
                com.sharednav.common.util.lerp(start, end, getFinalProgress(progress))
            },
            alpha = with(alpha) {
                androidx.compose.ui.util.lerp(start, end, getFinalProgress(progress))
            },
            position = with(position) {
                getFinalProgress(progress).let { finalProgress ->
                    TransformOrigin(
                        androidx.compose.ui.util.lerp(
                            start.pivotFractionX,
                            end.pivotFractionX,
                            finalProgress
                        ),
                        androidx.compose.ui.util.lerp(
                            start.pivotFractionY,
                            end.pivotFractionY,
                            finalProgress
                        )
                    )
                }
            },
            translationPercent = with(translationPercent) {
                getFinalProgress(progress).let { finalProgress ->
                    Offset(
                        androidx.compose.ui.util.lerp(start.x, end.x, finalProgress),
                        androidx.compose.ui.util.lerp(start.y, end.y, finalProgress)
                    )
                }
            },
            rotate = with(rotate) {
                getFinalProgress(progress).let { finalProgress ->
                    Rotation(
                        x = androidx.compose.ui.util.lerp(start.x, end.x, finalProgress),
                        y = androidx.compose.ui.util.lerp(start.y, end.y, finalProgress),
                        z = androidx.compose.ui.util.lerp(start.z, end.z, finalProgress),
                    )
                }
            },
            roll = with(roll) {
                getFinalProgress(progress).let { finalProgress ->
                    Roll(
                        top = androidx.compose.ui.util.lerp(start.top, end.top, finalProgress),
                        bottom = androidx.compose.ui.util.lerp(start.bottom, end.bottom, finalProgress),
                        left = androidx.compose.ui.util.lerp(start.left, end.left, finalProgress),
                        right = androidx.compose.ui.util.lerp(start.right, end.right, finalProgress),
                    )
                }
            },
            innerBlur = with(innerBlur) {
                androidx.compose.ui.unit.lerp(start, end, getFinalProgress(progress))
            },
        )
    }
}