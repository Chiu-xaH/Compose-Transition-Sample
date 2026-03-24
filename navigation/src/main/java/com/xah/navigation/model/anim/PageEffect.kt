package com.xah.navigation.model.anim

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp

/** lerp
 * 1. 预测式返回时：
 * 背景从 Background->PredictiveBackground 按predictiveProgress定进度
 * 主体从 Full->PredictiveSelf 按predictiveProgress定进度
 *
 * 2. 正常情况返回时：
 * 原来的背景从 Background->Full 按transition()动画播放
 * 原来的主体从 Full->None 按transition()动画播放
 *
 * 3. 正常情况前进时：
 * 原来的主体变背景，从Full->Background 按transition()动画播放
 * 主体从 None->Full 按transition()动画播放
 */
@Immutable
data class PageEffect(
    val scale: Float,
    val blur: Dp,
    val mask: Float,
    val corner : CornerBasedShape,
    val alpha : Float
)