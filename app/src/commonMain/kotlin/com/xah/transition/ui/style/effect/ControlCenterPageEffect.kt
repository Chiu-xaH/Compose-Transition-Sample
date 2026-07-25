package com.xah.transition.ui.style.effect

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import com.sharednav.common.helper.NoneRoundShape
import com.xah.navigation.model.anim.EffectLevel
import com.xah.navigation.model.anim.TransitionEffect
import com.xah.navigation.model.anim.effect.BackgroundPageEffectState
import com.xah.navigation.model.anim.effect.EffectValue
import com.xah.navigation.model.anim.effect.ForegroundPageEffectState
import com.xah.navigation.model.anim.effect.PageEffect
import com.xah.navigation.model.anim.effect.PageEffectFrame
import com.xah.navigation.model.anim.effect.PageEffects

const val CONTROL_CENTER_ALPHA = 0.125f
private val CONTROL_CENTER_BLUR = 42.5.dp
private const val CONTROL_CENTER_SCALE = 0.85f

data class ControlCenterTransitionEffect(
    val compositeOverColor : Color? = null,
    override val pageEffect : PageEffects = ControlCenterEffects(compositeOverColor),
    override val predictiveMinValue: Float = 0.85f,
    override val pushAnimation: AnimationSpec<Float> = tween(400),
    override val popAnimation: AnimationSpec<Float> = tween(400)
) : TransitionEffect

@Composable
fun rememberControlCenterEffects(): PageEffects = ControlCenterEffects(MaterialTheme.colorScheme.background)

fun ControlCenterEffects(
    compositeOverColor : Color? = null
) : PageEffects {

    return object : PageEffects(
        backgroundEffect = BackgroundPageEffectState(
            enableMirror = true,
            effect = PageEffect(
                corner = EffectValue.const(NoneRoundShape),
                scale = EffectValue(
                    start = 1f,
                    end = CONTROL_CENTER_SCALE
                ),
                maskLight = EffectValue(
                    start = Color.Transparent,
                    end = Color.Black.copy(CONTROL_CENTER_ALPHA),
                ),
                maskDark = EffectValue(
                    start = Color.Transparent,
                    end = Color.White.copy(CONTROL_CENTER_ALPHA),
                ),
                blur = EffectValue(
                    start = 0.dp,
                    end = CONTROL_CENTER_BLUR
                )
            )
        ),
        foregroundEffect = ForegroundPageEffectState(
            enableMirror = true,
            effect = PageEffect(
                corner = EffectValue.const(NoneRoundShape),
                scale = EffectValue(
                    start = CONTROL_CENTER_SCALE,
                    end = 1f
                ),
                blur = EffectValue(
                    start = CONTROL_CENTER_BLUR,
                    end = 0.dp
                ),
                alpha = EffectValue(
                    start = 0f,
                    end = 1f
                )
            )
        )
    ) {
        override fun background(progress: Float, level: EffectLevel): PageEffectFrame {
            return when(level) {
                EffectLevel.HIGH -> super.background(progress, level)
                EffectLevel.MEDIUM -> {
                    // 蒙层完全盖住 带缩放
                    backgroundEffect
                        .lerp(progress)
                        .copy(
                            blur = 0.dp,
                            innerBlur = 0.dp,
                            maskLight = lerp(Color.Transparent,Color.Black.copy(CONTROL_CENTER_ALPHA).compositeOver(compositeOverColor ?: Color.White),progress),
                            maskDark = lerp(Color.Transparent,Color.White.copy(CONTROL_CENTER_ALPHA).compositeOver(compositeOverColor ?: Color.Black),progress)
                        )
                }
                else -> {
                    // 蒙层完全盖住 无缩放
                    backgroundEffect
                        .lerp(progress)
                        .copy(
                            blur = 0.dp,
                            innerBlur = 0.dp,
                            scale = 1f,
                            maskLight = lerp(Color.Transparent,Color.Black.copy(CONTROL_CENTER_ALPHA).compositeOver(compositeOverColor ?: Color.White),progress),
                            maskDark = lerp(Color.Transparent,Color.White.copy(CONTROL_CENTER_ALPHA).compositeOver(compositeOverColor ?: Color.Black),progress)
                        )
                }
            }
        }
    }
}

