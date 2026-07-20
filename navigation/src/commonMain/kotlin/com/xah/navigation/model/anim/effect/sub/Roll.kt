package com.xah.navigation.model.anim.effect.sub

import androidx.compose.runtime.Immutable

/**
 * 描述页面四边被遮挡的比例，用于实现 Reveal 揭示转场效果。
 *
 * 每个值范围 [0f, 1f]：
 * - 0f = 该边完全不遮挡
 * - 1f = 该边完全遮挡
 *
 * 例如从下方 reveal（幕布从下往上拉起）：
 * - start = ClipReveal(top = 1f)  — 顶部 100% 被遮挡，什么也看不到
 * - end   = ClipReveal.None       — 完全可见
 */
@Immutable
data class Roll(
    val top: Float = 0f,
    val bottom: Float = 0f,
    val left: Float = 0f,
    val right: Float = 0f,
) {
    companion object Companion {
        val None = Roll()
    }
}
