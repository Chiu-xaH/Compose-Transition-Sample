package com.xah.navigation.model.anim

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sharednav.common.helper.ScreenCornerHelper
import kotlin.math.abs

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
data class PageEffectFrame(
    val scale: Float = 1f,
    val blur: Dp = 0.dp,
    val mask: Float = 0f,
    val corner: CornerBasedShape = RoundedCornerShape(ScreenCornerHelper.corner),
    val alpha: Float = 1f,
    val position: TransformOrigin = TransformOrigin.Center,
    val translationPercent: Offset = Offset.Zero,
    val rotate: Rotation = Rotation(),
    // TODO RenderEffect
)

