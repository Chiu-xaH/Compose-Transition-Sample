package com.xah.transition.ui.screen.test

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private const val MORPH_POINT_COUNT = 240

private data class ShapeKeyframe(
    val shape: Shape,
    val center: Offset,
    val size: Size
)

private class StarShape(
    private val points: Int = 5,
    private val innerRadiusRatio: Float = 0.46f
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val center = Offset(size.width / 2f, size.height / 2f)
        val outerRadius = min(size.width, size.height) / 2f
        val innerRadius = outerRadius * innerRadiusRatio
        val path = Path()
        val vertexCount = points * 2

        repeat(vertexCount) { index ->
            val angle = -PI / 2.0 + 2.0 * PI * index / vertexCount
            val radius = if (index % 2 == 0) outerRadius else innerRadius
            val point = Offset(
                x = center.x + cos(angle).toFloat() * radius,
                y = center.y + sin(angle).toFloat() * radius
            )
            if (index == 0) {
                path.moveTo(point.x, point.y)
            } else {
                path.lineTo(point.x, point.y)
            }
        }
        path.close()
        return Outline.Generic(path)
    }
}

private fun Shape.toLocalPath(
    size: Size,
    layoutDirection: LayoutDirection,
    density: Density
): Path {
    return Path().apply {
        addOutline(createOutline(size, layoutDirection, density))
    }
}

private fun samplePathByLength(path: Path, count: Int): List<Offset> {
    val measure = PathMeasure()
    measure.setPath(path, forceClosed = true)
    val length = measure.length
    if (length <= 0f) {
        return List(count) { Offset.Zero }
    }

    return List(count) { index ->
        val distance = length * index / count
        measure.getPosition(distance)
    }
}

private fun ShapeKeyframe.sampleContour(
    pointCount: Int,
    layoutDirection: LayoutDirection,
    density: Density
): List<Offset> {
    val localPath = shape.toLocalPath(size, layoutDirection, density)
    val localPoints = samplePathByLength(localPath, pointCount)
    val topLeft = Offset(center.x - size.width / 2f, center.y - size.height / 2f)
    return localPoints.map { point -> point + topLeft }
}

private fun lerpOffset(start: Offset, end: Offset, progress: Float): Offset {
    return Offset(
        x = start.x + (end.x - start.x) * progress,
        y = start.y + (end.y - start.y) * progress
    )
}

private fun ShapeKeyframe.interpolateTo(
    other: ShapeKeyframe,
    progress: Float,
    layoutDirection: LayoutDirection,
    density: Density,
    pointCount: Int = MORPH_POINT_COUNT
): Path {
    val fromPoints = sampleContour(pointCount, layoutDirection, density)
    val toPoints = other.sampleContour(pointCount, layoutDirection, density)
    val path = Path()

    fromPoints.indices.forEach { index ->
        val point = lerpOffset(fromPoints[index], toPoints[index], progress)
        if (index == 0) {
            path.moveTo(point.x, point.y)
        } else {
            path.lineTo(point.x, point.y)
        }
    }
    path.close()
    return path
}

private fun morphShapePath(
    from: ShapeKeyframe,
    to: ShapeKeyframe,
    progress: Float,
    layoutDirection: LayoutDirection,
    density: Density,
    pointCount: Int = MORPH_POINT_COUNT
): Path {
    val middleSizeValue = min(
        min(from.size.width, from.size.height),
        min(to.size.width, to.size.height)
    )
    val middle = ShapeKeyframe(
        shape = CircleShape,
        center = lerpOffset(from.center, to.center, 0.5f),
        size = Size(middleSizeValue, middleSizeValue)
    )

    return if (progress < 0.5f) {
        from.interpolateTo(
            other = middle,
            progress = progress / 0.5f,
            layoutDirection = layoutDirection,
            density = density,
            pointCount = pointCount
        )
    } else {
        middle.interpolateTo(
            other = to,
            progress = (progress - 0.5f) / 0.5f,
            layoutDirection = layoutDirection,
            density = density,
            pointCount = pointCount
        )
    }
}

@Preview
@Composable
fun ShapeLerpDemo() {
    var forward by remember { mutableStateOf(true) }
    var replayKey by remember { mutableIntStateOf(0) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(forward, replayKey) {
        progress.animateTo(
            targetValue = if (forward) 1f else 0f,
            animationSpec = tween(
                durationMillis = 1800,
                easing = FastOutSlowInEasing
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080B14))
            .navigationBarsPadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Shape Path Morph",
                color = Color(0xFFEAF4FF),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "any Shape first morphs into a circle, then morphs into the target Shape",
                color = Color(0xFF8EA0B8),
                fontSize = 12.sp
            )
            Spacer(Modifier.height(28.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Color(0xFF101828), RoundedCornerShape(28.dp))
                    .clickable { forward = !forward }
                    .padding(8.dp)
            ) {
                val minSide = min(size.width, size.height)
                val from = ShapeKeyframe(
                    shape = StarShape(points = 5, innerRadiusRatio = 0.45f),
                    center = Offset(size.width * 0.25f, size.height * 0.50f),
                    size = Size(minSide * 0.26f, minSide * 0.26f)
                )
                val to = ShapeKeyframe(
                    shape = RoundedCornerShape(minSide * 0.045f),
                    center = Offset(size.width * 0.75f, size.height * 0.50f),
                    size = Size(minSide * 0.38f, minSide * 0.24f)
                )
                val path = morphShapePath(
                    from = from,
                    to = to,
                    progress = progress.value,
                    layoutDirection = layoutDirection,
                    density = this
                )

                drawPath(
                    path = path,
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFFFD166),
                            Color(0xFF00D5FF),
                            Color(0xFF9B35FF)
                        ),
                        start = Offset.Zero,
                        end = Offset(size.width, size.height)
                    )
                )
                drawPath(
                    path = path,
                    color = Color.White.copy(alpha = 0.18f),
                    style = Stroke(width = 1.5.dp.toPx())
                )
            }

            Spacer(Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { forward = !forward },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0A84FF)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text(
                        text = if (forward) "Morph Back" else "Morph Forward",
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Button(
                    onClick = { replayKey++ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263244)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Replay", fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Example: StarShape → CircleShape → RoundedCornerShape",
                color = Color(0xFF5E6B7E),
                fontSize = 11.sp
            )
        }
    }
}
