package com.xah.container.anim

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.util.lerp

typealias RectInterpolator = (progress: Float, from: Rect, to: Rect) -> Rect

/**
 * 线性路径插值
 */
val LinearRectInterpolator: RectInterpolator = { t, from, to ->
    Rect(
        left = lerp(from.left, to.left, t),
        top = lerp(from.top, to.top, t),
        right = lerp(from.right, to.right, t),
        bottom = lerp(from.bottom, to.bottom, t)
    )
}

/** 二次贝塞尔路径插值
 * @param screenRect 屏幕Rect
 * @param maxVerticalArc 垂直限制系数
 * @param maxHorizontalArc 水平限制系数
 */
fun QuadraticBezierRectInterpolator(
    screenRect : Rect,
    maxVerticalArc: Float = 2f,
    maxHorizontalArc: Float = 3f
): RectInterpolator = { t, from, to ->
    val startCenter = Offset(
        from.left + from.width / 2f,
        from.top + from.height / 2f
    )
    val endCenter = Offset(
        to.left + to.width / 2f,
        to.top + to.height / 2f
    )

    val avgY = (startCenter.y + endCenter.y) / 2f
    val normalizedY = ((avgY / screenRect.height) - 0.5f) * 2f
    val verticalArc = -normalizedY * (to.height / maxVerticalArc)

    val avgX = (startCenter.x + endCenter.x) / 2f
    val normalizedX = ((avgX / screenRect.width) - 0.5f) * 2f
    val horizontalArc = -normalizedX * (to.width / maxHorizontalArc)

    // 控制点
    val control = Offset(
        x = (startCenter.x + endCenter.x) / 2f + horizontalArc,
        y = (startCenter.y + endCenter.y) / 2f + verticalArc
    )

    val oneMinusT = 1f - t

    val center = Offset(
        x = oneMinusT * oneMinusT * startCenter.x + 2 * oneMinusT * t * control.x + t * t * endCenter.x,
        y = oneMinusT * oneMinusT * startCenter.y + 2 * oneMinusT * t * control.y + t * t * endCenter.y
    )

    val width = lerp(from.width, to.width, t)
    val height = lerp(from.height, to.height, t)

    val half = 2f

    Rect(
        left = center.x - width / half,
        top = center.y - height / half,
        right = center.x + width / half,
        bottom = center.y + height / half
    )
}