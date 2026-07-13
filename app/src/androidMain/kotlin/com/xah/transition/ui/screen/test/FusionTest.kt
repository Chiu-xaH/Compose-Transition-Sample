package com.xah.transition.ui.screen.test

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

private const val FUSION_SHADER = """
    uniform float2 u_resolution;
    uniform float2 u_circlePos;
    uniform float2 u_rectPos;


    float sdRoundedRect(float2 p, float2 b, float r) {
        float2 q = abs(p) - b + r;
        return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - r;
    }

    float sdCircle(float2 p, float r) {
        return length(p) - r;
    }

    float smin(float a, float b, float k) {
        float h = clamp(0.5 + 0.5 * (b - a) / k, 0.0, 1.0);
        return mix(b, a, h) - k * h * (1.0 - h);
    }

    half4 main(float2 fragCoord) {
        float2 uv = fragCoord / u_resolution;
        float2 p = (uv - 0.5) * 2.0;
        p.x *= u_resolution.x / u_resolution.y;

        float circleR = 0.20;
        float rectW = 0.40;
        float rectH = 0.3;
        float rectR = 0.05;

        float d_c = sdCircle(p - u_circlePos, circleR);
        float d_r = sdRoundedRect(p - u_rectPos, float2(rectW, rectH), rectR);

        // ══════════════════════════════════════════════════════
        //  Distance-driven fusion:
        //    Far apart  → blobK ≈ 0  → no bridge, shapes independent
        //    Close      → blobK ↑     → thick organic bridge
        // ══════════════════════════════════════════════════════
        float dist = length(u_rectPos - u_circlePos);
        float blobK = max(0.001, smoothstep(1.8, 0.3, dist) * 0.25);

        // Color blend factor (mirrors smin's internal h)
        float h = blobK > 0.01
            ? clamp(0.5 + 0.5 * (d_r - d_c) / blobK, 0.0, 1.0)
            : step(d_c, d_r);

        float d = smin(d_c, d_r, blobK);



        // AA
        float aaWidth = 1.5 / u_resolution.y;
        float alpha = 1.0 - smoothstep(0.0, aaWidth, d);

        // Two-tone: blue circle, purple rect, blended at bridge
        half3 circleCol = half3(0.04, 0.52, 1.0);
        half3 rectCol = half3(0.58, 0.20, 0.93);
        half3 col = mix(rectCol, circleCol, half(h));

        return half4(col, half(alpha));
    }
"""

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun FusionDemo() {
    var circlePos by remember { mutableStateOf(Offset(0.55f, -0.35f)) }
    var rectPos by remember { mutableStateOf(Offset(-0.55f, 0.3f)) }
    var dragTarget by remember { mutableStateOf<String?>(null) }

    val shader = remember { RuntimeShader(FUSION_SHADER) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            // Pixel → shader space
                            val res = size
                            val aspect = res.width / res.height
                            val sx = (2f * offset.x / res.width - 1f) * aspect
                            val sy = 2f * offset.y / res.height - 1f

                            // SDF hit test
                            val dCircle = sqrt(
                                (sx - circlePos.x).pow(2) +
                                        (sy - circlePos.y).pow(2)
                            ) - 0.20f

                            val dx = abs(sx - rectPos.x) - 0.55f
                            val dy = abs(sy - rectPos.y) - 0.40f
                            val dRect = if (dx < 0f && dy < 0f) {
                                -min(-dx, -dy)
                            } else {
                                sqrt(max(dx, 0f).pow(2) + max(dy, 0f).pow(2))
                            }

                            dragTarget = when {
                                dCircle < 0f && dRect < 0f ->
                                    if (dCircle < dRect) "circle" else "rect"
                                dCircle < 0f -> "circle"
                                dRect < 0f -> "rect"
                                else -> if (dCircle < dRect) "circle" else "rect"
                            }
                        },
                        onDrag = { _, dragAmount ->
                            // Pixel delta → shader space delta
                            val res = size
                            val dsx = dragAmount.x * 2f / res.height
                            val dsy = dragAmount.y * 2f / res.height

                            when (dragTarget) {
                                "circle" -> circlePos = Offset(
                                    circlePos.x + dsx,
                                    circlePos.y + dsy
                                )
                                "rect" -> rectPos = Offset(
                                    rectPos.x + dsx,
                                    rectPos.y + dsy
                                )
                            }
                        },
                        onDragEnd = { dragTarget = null },
                        onDragCancel = { dragTarget = null }
                    )
                }
        ) {
            shader.setFloatUniform("u_resolution", size.width, size.height)
            shader.setFloatUniform("u_circlePos", circlePos.x, circlePos.y)
            shader.setFloatUniform("u_rectPos", rectPos.x, rectPos.y)

            drawRect(brush = ShaderBrush(shader), size = size)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                circlePos = Offset(0.55f, -0.35f)
                rectPos = Offset(-0.55f, 0.3f)
            },
        ) {
            Text("Reset Positions", fontWeight = FontWeight.Medium)
        }

        Spacer(Modifier.navigationBarsPadding().height(16.dp))
    }
}