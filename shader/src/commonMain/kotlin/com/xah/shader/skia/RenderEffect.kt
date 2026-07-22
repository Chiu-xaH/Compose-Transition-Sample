package com.xah.shader.skia

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.RenderEffect
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp

expect fun RenderEffect?.chain(other: RenderEffect): RenderEffect

expect fun RuntimeShaderEffect(
    runtimeShader: RuntimeShader,
    uniformShaderName: String
): RenderEffect

expect val canUseBlurRenderEffect : Boolean

fun BlurRenderEffect(
    radiusX: Float,
    radiusY: Float,
    edgeTreatment: TileMode = TileMode.Clamp
) = BlurEffect(radiusX,radiusY,edgeTreatment)

fun BlurRenderEffect(
    radius: Float,
    edgeTreatment: TileMode = TileMode.Clamp
) = BlurRenderEffect(radius,radius,edgeTreatment)

fun BlurRenderEffect(
    radius: Float,
) = BlurRenderEffect(radius,radius)

fun BlurRenderEffect(
    density: Density,
    radiusX: Dp,
    radiusY: Dp,
    edgeTreatment: TileMode = TileMode.Clamp
): RenderEffect = with(density) {
    BlurEffect(
        radiusX.toPx(),
        radiusY.toPx(),
        edgeTreatment
    )
}

fun BlurRenderEffect(
    density: Density,
    radius: Dp,
    edgeTreatment: TileMode = TileMode.Clamp
) = BlurRenderEffect(density,radius, radius, edgeTreatment)

fun BlurRenderEffect(
    density: Density,
    radius: Dp,
) = BlurRenderEffect(density,radius, radius)

@Composable
fun BlurRenderEffect(
    radiusX: Dp,
    radiusY: Dp,
    edgeTreatment: TileMode = TileMode.Clamp
): RenderEffect = with(LocalDensity.current) {
    BlurEffect(
        radiusX.toPx(),
        radiusY.toPx(),
        edgeTreatment
    )
}

@Composable
fun BlurRenderEffect(
    radius: Dp,
    edgeTreatment: TileMode = TileMode.Clamp
) = BlurRenderEffect(radius, radius, edgeTreatment)

@Composable
fun BlurRenderEffect(
    radius: Dp,
) = BlurRenderEffect(radius, radius)