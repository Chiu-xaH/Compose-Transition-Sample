package com.xah.navigation.model.anim.effect.sub

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection

internal class RollShape(
    private val roll: Roll,
    private val corner: CornerBasedShape,
) : Shape {

    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val visibleLeft   = size.width  * roll.left
        val visibleTop    = size.height * roll.top
        val visibleRight  = size.width  * (1f - roll.right)
        val visibleBottom = size.height * (1f - roll.bottom)

        // 如果可见区域为零或无效，返回空矩形
        if (visibleRight <= visibleLeft || visibleBottom <= visibleTop) {
            return Outline.Rectangle(Rect(0f, 0f, 0f, 0f))
        }

        // 将 corner shape 应用到可见区域矩形上，圆角跟随裁剪边界移动
        return corner.createOutline(
            size = Size(visibleRight - visibleLeft, visibleBottom - visibleTop),
            layoutDirection = layoutDirection,
            density = density
        ).let { outline ->
            when(outline) {
                is Outline.Rectangle -> Outline.Rectangle(
                    Rect(visibleLeft, visibleTop, visibleRight, visibleBottom)
                )
                is Outline.Rounded -> {
                    val rr = outline.roundRect
                    Outline.Rounded(
                        RoundRect(
                            left = rr.left + visibleLeft,
                            top = rr.top + visibleTop,
                            right = rr.right + visibleLeft,
                            bottom = rr.bottom + visibleTop,
                            topLeftCornerRadius = rr.topLeftCornerRadius,
                            topRightCornerRadius = rr.topRightCornerRadius,
                            bottomRightCornerRadius = rr.bottomRightCornerRadius,
                            bottomLeftCornerRadius = rr.bottomLeftCornerRadius,
                        )
                    )
                }
                is Outline.Generic -> outline // path based 不做偏移
            }
        }
    }
}