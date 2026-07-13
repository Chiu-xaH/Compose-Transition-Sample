package com.xah.transition.ui.screen.test

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private const val SINGLE_SHAPE_FLOW_SHADER = """
    uniform float2 u_resolution;
    uniform float u_progress;
    uniform float u_time;

    float sdCircle(float2 p, float r) {
        return length(p) - r;
    }

    float sdRoundRect(float2 p, float2 b, float r) {
        float2 q = abs(p) - b + float2(r);
        return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
    }

    float smoothMin(float a, float b, float k) {
        float h = clamp(0.5 + 0.5 * (b - a) / k, 0.0, 1.0);
        return mix(b, a, h) - k * h * (1.0 - h);
    }

    float phase(float start, float end, float t) {
        float x = clamp((t - start) / (end - start), 0.0, 1.0);
        return x * x * (3.0 - 2.0 * x);
    }

    half4 main(float2 fragCoord) {
        float2 p = fragCoord;
        float t = clamp(u_progress, 0.0, 1.0);
        float minSide = min(u_resolution.x, u_resolution.y);

        // 唯一形状的起点和终点：左边圆形 → 右边圆角矩形。
        float2 startCenter = float2(u_resolution.x * 0.24, u_resolution.y * 0.50);
        float2 endCenter = float2(u_resolution.x * 0.76, u_resolution.y * 0.50);
        float2 axis = endCenter - startCenter;
        float axisLen = max(length(axis), 0.001);
        float2 dir = axis / axisLen;
        float2 normal = float2(-dir.y, dir.x);

        float circleR = minSide * 0.105;
        float2 rectHalf = float2(minSide * 0.170, minSide * 0.105);
        float rectR = minSide * 0.035;

        float2 center = mix(startCenter, endCenter, t);
        float liquid = sin(t * 3.14159265);

        // 主体：同一个局部坐标下，圆 SDF 逐渐变成圆角矩形 SDF。
        float2 local = p - center;
        float dCircle = sdCircle(local, circleR);
        float dRect = sdRoundRect(local, rectHalf, rectR);
        float d = mix(dCircle, dRect, t);

        // 保持为单一主体形变：不再额外生成液滴/小尾巴。
        // 只保留主体在中段的一点液态扰动，让运动过程更柔和。

        // 细微液面扰动，只影响中间，保证首尾形态准确。
        float ripple = sin(dot(p, dir) * 0.032 - u_time * 4.0) * sin(dot(p, normal) * 0.050 + u_time * 2.0);
        d += ripple * liquid * minSide * 0.0016;

        float aa = 1.7;
        float alpha = 1.0 - smoothstep(-aa, aa, d);

        half3 blue = half3(0.02, 0.48, 1.00);
        half3 cyan = half3(0.00, 0.82, 1.00);
        half3 purple = half3(0.58, 0.20, 1.00);
        half3 color = mix(blue, purple, half(t * 0.78));
        color = mix(color, cyan, half(liquid * 0.32));

        float2 highlightPoint = center + dir * mix(circleR * 0.45, rectHalf.x * 0.45, t);
        float highlight = 1.0 - smoothstep(0.0, minSide * 0.22, length(p - highlightPoint));
        color += half3(0.18, 0.24, 0.30) * half(highlight * liquid * 0.32);

        return half4(color, half(alpha));
    }
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ShapeFusionDemo() {
    var forward by remember { mutableStateOf(true) }
    var replayKey by remember { mutableIntStateOf(0) }
    val progress = remember { Animatable(0f) }
    val time by produceState(0f) {
        while (true) {
            withFrameNanos { value = it / 1_000_000_000f }
        }
    }
    val shader = remember { RuntimeShader(SINGLE_SHAPE_FLOW_SHADER) }

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
                text = "Single SDF Morph",
                color = Color(0xFFEAF4FF),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "one object: left circle → right rounded rect",
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
                shader.setFloatUniform("u_resolution", size.width, size.height)
                shader.setFloatUniform("u_progress", progress.value)
                shader.setFloatUniform("u_time", time)
                drawRect(brush = ShaderBrush(shader), size = size)
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
                    onClick = {
                        replayKey++
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263244)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Text("Replay", fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "AGSL RuntimeShader: one SDF field, circle-to-rect liquid morph",
                color = Color(0xFF5E6B7E),
                fontSize = 11.sp
            )
        }
    }
}
