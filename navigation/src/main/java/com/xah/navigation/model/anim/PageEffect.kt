package com.xah.navigation.model.anim

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class PageEffect(
    val corner: EffectValue<CornerBasedShape>,
    val scale: EffectValue<Float> = EffectValue.const(1f),
    val blur: EffectValue<Dp> = EffectValue.const(0.dp),
    val mask: EffectValue<Float> = EffectValue.const(0f),
    val alpha: EffectValue<Float> = EffectValue.const(1f),
    val position: EffectValue<TransformOrigin> = EffectValue.const(TransformOrigin.Center),
    val translationPercent: EffectValue<Offset> = EffectValue.const(Offset.Zero),
    val rotate: EffectValue<Rotation> = EffectValue.const(Rotation()),
)