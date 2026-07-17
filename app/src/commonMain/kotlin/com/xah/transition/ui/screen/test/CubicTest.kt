package com.xah.transition.ui.screen.test

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.xah.transition.ui.component.APP_HORIZONTAL_DP
import com.xah.transition.ui.component.CustomSlider
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun CubicBezierEditor(
    x1: Float,
    y1: Float,
    x2: Float,
    y2: Float,
    onChangeX1: (Float) -> Unit,
    onChangeY1: (Float) -> Unit,
    onChangeX2: (Float) -> Unit,
    onChangeY2: (Float) -> Unit
) {
    val easing = remember(x1, y1, x2, y2) {
        CubicBezierEasing(x1, y1, x2, y2)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        BezierCanvas(
            x1 = x1,
            y1 = y1,
            x2 = x2,
            y2 = y2,
            easing = easing,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = APP_HORIZONTAL_DP)
                .clip(RoundedCornerShape(APP_HORIZONTAL_DP,0.dp,APP_HORIZONTAL_DP,0.dp))
                .height(250.dp)
        )

        Spacer(Modifier.height(24.dp))

        SliderItem("x1", x1) {
            onChangeX1(it)
        }
        SliderItem("y1", y1) {
            onChangeY1(it)
        }
        SliderItem("x2", x2) {
            onChangeX2(it)
        }
        SliderItem("y2", y2) {
            onChangeY2(it)
        }
    }
}


@Composable
@Preview
fun CubicBezierEditorDemo() {
    var x1 by remember { mutableFloatStateOf(0.25f) }
    var y1 by remember { mutableFloatStateOf(0.1f) }
    var x2 by remember { mutableFloatStateOf(0.25f) }
    var y2 by remember { mutableFloatStateOf(1f) }

    val easing = remember(x1, y1, x2, y2) {
        CubicBezierEasing(x1, y1, x2, y2)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("CubicBezierEasing 可视化", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        BezierCanvas(
            x1 = x1,
            y1 = y1,
            x2 = x2,
            y2 = y2,
            easing = easing,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        )

        Spacer(Modifier.height(APP_HORIZONTAL_DP))

        SliderItem2("x1", x1) { x1 = it }
        SliderItem2("y1", y1) { y1 = it }
        SliderItem2("x2", x2) { x2 = it }
        SliderItem2("y2", y2) { y2 = it }

        Spacer(Modifier.height(32.dp))

        Text("动画演示", style = MaterialTheme.typography.titleMedium)

        Spacer(Modifier.height(16.dp))

        BezierBallDemo(easing = easing)
    }
}

@Composable
private fun SliderItem2(
    label: String,
    value: Float,
    onChange: (Float) -> Unit
) {
    Column {
        Text("$label: ${(value)}")
        Slider(
            value = value,
            onValueChange = onChange,
            valueRange = 0f..1f
        )
    }
}
@Composable
private fun SliderItem(
    label: String,
    value: Float,
    onChange: (Float) -> Unit
) {
    Column {
        Text("$label: ${(value)}", modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP))
        CustomSlider(
            value = value,
            onValueChange = onChange,
            valueRange = 0f..1f
        )
    }
}

@Composable
private fun BezierCanvas(
    x1: Float,
    y1: Float,
    x2: Float,
    y2: Float,
    easing: Easing,
    modifier: Modifier = Modifier
) {
    val primary = MaterialTheme.colorScheme.primary
    val error = MaterialTheme.colorScheme.error
    val errorContainer = MaterialTheme.colorScheme.error.copy(.5f)
    Canvas(modifier = modifier.background(MaterialTheme.colorScheme.primaryContainer)) {

        val w = size.width
        val h = size.height

        fun Offset.scale() = Offset(x * w, h - y * h)

        // 坐标轴
//        drawLine(errorContainer, Offset(0f, h), Offset(w, h), strokeWidth = 2f)
//        drawLine(errorContainer, Offset(0f, 0f), Offset(0f, h), strokeWidth = 2f)

        val p0 = Offset(0f, 0f).scale()
        val p1 = Offset(x1, y1).scale()
        val p2 = Offset(x2, y2).scale()
        val p3 = Offset(1f, 1f).scale()

        // 控制线
        drawLine(errorContainer, p0, p1, strokeWidth = 2f)
        drawLine(errorContainer, p3, p2, strokeWidth = 2f)

        // 贝塞尔路径
        val path = Path().apply {
            moveTo(p0.x, p0.y)
            cubicTo(
                p1.x, p1.y,
                p2.x, p2.y,
                p3.x, p3.y
            )
        }

        drawPath(
            path = path,
            color = primary,
            style = Stroke(width = 4f)
        )

        // 控制点
        drawCircle(error, radius = 8f, center = p1)
        drawCircle(error, radius = 8f, center = p2)

        // 显示 easing 采样曲线
        val samplePath = Path()
        for (i in 0..100) {
            val t = i / 100f
            val y = easing.transform(t)
            val point = Offset(t, y).scale()

            if (i == 0) samplePath.moveTo(point.x, point.y)
            else samplePath.lineTo(point.x, point.y)
        }

        drawPath(
            path = samplePath,
            color = primary,
            style = Stroke(width = 3f)
        )
    }
}

@Composable
private fun BezierBallDemo(
    easing: Easing
) {
    val infiniteTransition = rememberInfiniteTransition(label = "ball")

    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 500,
                easing = easing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "ballProgress"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(Color(0xFF1A1A1A)),
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        x = (progress * 600).toInt(),
                        y = 0
                    )
                }
                .size(24.dp)
                .background(Color.Magenta, CircleShape)
        )
    }
}