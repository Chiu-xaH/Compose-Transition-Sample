package com.xah.container.logic

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.util.lerp

typealias RectInterpolator = (progress: Float, from: Rect, to: Rect) -> Rect

val LinearRectInterpolator: RectInterpolator = { t, from, to ->
    Rect(
        left = lerp(from.left, to.left, t),
        top = lerp(from.top, to.top, t),
        right = lerp(from.right, to.right, t),
        bottom = lerp(from.bottom, to.bottom, t)
    )
}

fun AdaptiveBezierRectInterpolator(
    screenHeight: Float,
    maxArc: Float = 1250f
): RectInterpolator = { t, from, to ->

    val startCenter = Offset(
        from.left + from.width / 2f,
        from.top + from.height / 2f
    )

    val endCenter = Offset(
        to.left + to.width / 2f,
        to.top + to.height / 2f
    )

    // ===== 1️⃣ 计算整体位置 =====
    val avgY = (startCenter.y + endCenter.y) / 2f

    // 归一化到 [-1 , 1]
    val normalized = ((avgY / screenHeight) - 0.5f) * 2f

    // 反向一下，使顶部为正（下沉）
    val dynamicArc = -normalized * maxArc

    // ===== 2️⃣ 控制点 =====
    val control = Offset(
        x = (startCenter.x + endCenter.x) / 2f,
        y = (startCenter.y + endCenter.y) / 2f + dynamicArc
    )

    val oneMinusT = 1f - t

    val center = Offset(
        x = oneMinusT * oneMinusT * startCenter.x +
                2 * oneMinusT * t * control.x +
                t * t * endCenter.x,
        y = oneMinusT * oneMinusT * startCenter.y +
                2 * oneMinusT * t * control.y +
                t * t * endCenter.y
    )

    val width = lerp(from.width, to.width, t)
    val height = lerp(from.height, to.height, t)

    Rect(
        left = center.x - width / 2f,
        top = center.y - height / 2f,
        right = center.x + width / 2f,
        bottom = center.y + height / 2f
    )
}
