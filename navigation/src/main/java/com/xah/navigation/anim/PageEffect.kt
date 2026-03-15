package com.xah.navigation.anim

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import com.sharednav.common.ScreenCornerHelper
import com.sharednav.common.lerp

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

@Immutable
data class PageEffectState(
    val start: PageEffect,
    val end: PageEffect,
) {
    fun lerp(progress : Float) = PageEffect(
        scale = lerp(start.scale,end.scale,progress),
        blur = lerp(start.blur,end.blur,progress),
        mask = lerp(start.mask,end.mask,progress),
        corner = lerp(start.corner,end.corner,progress),
        alpha = lerp(start.alpha,end.alpha,progress),
    )
}

@Immutable
data class PageEffects(
    val backgroundEffect : PageEffectState,
    val foregroundEffect : PageEffectState,
    val foregroundOrigin : TransformOrigin
) {
    fun background(progress : Float,level: EffectLevel) =
        backgroundEffect
            .lerp(progress)
            .let {
                when(level) {
                    EffectLevel.FULL -> {
                        it
                    }
                    EffectLevel.NO_BLUR -> {
                        it.copy(blur = 0.dp)
                    }
                    EffectLevel.NO_SCALE -> {
                        it.copy(blur = 0.dp, scale = 1f)
                    }
                    EffectLevel.NONE -> {
                        it.copy(blur = 0.dp, scale = 1f)
                    }
                }
            }

    fun foreground(progress : Float,level: EffectLevel) =
        foregroundEffect
            .let {
                when(level) {
                    EffectLevel.FULL -> {
                        it.lerp(progress)
                    }
                    EffectLevel.NO_BLUR -> {
                        it.lerp(progress).copy(blur = 0.dp)
                    }
                    EffectLevel.NO_SCALE -> {
                        it.lerp(progress).copy(blur = 0.dp)
                    }
                    EffectLevel.NONE -> {
                        PageEffect(
                            scale = lerp(backgroundEffect.end.scale,1f,progress),
                            blur = 0.dp,
                            mask = lerp(it.start.mask,it.end.mask,progress),
                            corner = it.end.corner,
                            alpha = lerp(0f,1f,progress),
                        )
                    }
                }
            }

    fun foregroundOrigin(level: EffectLevel) = if(level == EffectLevel.NONE) TransformOrigin(0.5f,0.5f) else foregroundOrigin
}

@Composable
fun rememberDefaultPageEffects(): PageEffects {
    val corner = ScreenCornerHelper.corner
    return remember(corner) {
        DefaultPageEffects(corner)
    }
}

private fun DefaultPageEffects(corner : Dp) : PageEffects {
    return PageEffects(
        backgroundEffect = PageEffectState(
            start = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(0.dp),
                alpha = 1f,
            ),
            end = PageEffect(
                scale = 0.875f,
                blur = 25.dp,
                mask = 0.25f,
                corner = RoundedCornerShape(0.dp),
                alpha = 1f
            )
        ),
        foregroundEffect = PageEffectState(
            start = PageEffect(
                scale = 0f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner*2.25f),
                alpha = 0.75f
            ),
            end = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner),
                alpha = 1f,
            )
        ),
        foregroundOrigin = TransformOrigin(0.5f,0.275f)
    )
}


/**
 * 前景带模糊版，性能警告
 */
@Composable
fun rememberDefaultPageEffectsEnhance(): PageEffects {
    val corner = ScreenCornerHelper.corner
    return remember(corner) {
        DefaultPageEffectsEnhance(corner)
    }
}

private fun DefaultPageEffectsEnhance(corner : Dp) : PageEffects {
    return PageEffects(
        backgroundEffect = PageEffectState(
            start = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(0.dp),
                alpha = 1f,
            ),
            end = PageEffect(
                scale = 0.875f,
                blur = 25.dp,
                mask = 0.25f,
                corner = RoundedCornerShape(0.dp),
                alpha = 1f
            )
        ),
        foregroundEffect = PageEffectState(
            start = PageEffect(
                scale = 0f,
                blur = 20.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner*2.25f),
                alpha = 1f
            ),
            end = PageEffect(
                scale = 1f,
                blur = 0.dp,
                mask = 0f,
                corner = RoundedCornerShape(corner),
                alpha = 1f,
            )
        ),
        foregroundOrigin = TransformOrigin(0.5f,0.275f)
    )
}