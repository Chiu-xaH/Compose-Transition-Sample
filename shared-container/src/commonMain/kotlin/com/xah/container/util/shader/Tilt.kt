package com.xah.container.util.shader

import androidx.compose.ui.graphics.RenderEffect
import com.xah.shader.skia.RuntimeShader
import com.xah.shader.skia.RuntimeShaderEffect
import kotlin.math.abs

/**
 * iOS26 风格的非线性扭曲（软体拖拽）
 *
 * 与「倾斜(rotationX/rotationY)」共用同一套方向与强度语义：
 * - rotationY 决定横向被拖拽的方向：>= 0 拖向右，< 0 拖向左
 * - rotationX 决定纵向被拖拽的方向：>= 0 拖向上，< 0 拖向下
 * 二者的绝对值除以 [maxTilt] 得到该轴的形变强度 0~1。
 *
 * 形变模型（"两边弯、两边直"）：
 *
 * 核心约束：
 *   - 平行于拖拽方向的两条边 → 保持直线
 *   - 垂直于拖拽方向的两条边 → 弯成曲线
 *
 * 实现原理：
 *   纯纵向拉伸（dir.x=0, dir.y≠0）为例——
 *   · disp.y（沿拖拽轴）：由 gy 控制拉多拉少（拖拽边多、对边少），与 x 无关
 *     → 同一列各行位移不同，上/下边弯曲 ✓
 *     → 但 disp.y 此时与 x 无关，左右边每行位移相同，仍是直线 ✓
 *   · disp.x（垂直于拖拽轴）：颈缩，让中间的内容向中线靠拢、两侧不动
 *     → disp.x 必须随 y 变化（靠近拖拽边时收拢越多）
 *     → 对于左右两条竖边（x=0 和 x=W），颈缩量相同（关于中线对称）→ 竖边仍直线 ✓
 *     → 对于上下两条横边（y=0 和 y=H）：下边 gy≈0 几乎没有颈缩，上边 gy=1 颈缩最强
 *       → 横边各点 x 位移不同 → 上下边弯曲 ✓
 *
 * 斜向拉伸（dir.x≠0, dir.y≠0）：两轴叠加，形变集中到拖拽方向的那个角。
 *
 * @param rotationX X 轴旋转值，控制纵向拖拽方向与强度
 * @param rotationY Y 轴旋转值，控制横向拖拽方向与强度
 * @param width  形变区域宽度（px）
 * @param height 形变区域高度（px）
 * @param isHorizontal
 *        - true  只做纵向形变（左右边直，上下边弯）
 *        - false 只做横向形变（上下边直，左右边弯）
 *        - null  双轴自由形变
 * @param warpCurve 扭曲曲线，1f 线性，< 1f 更软，> 1f 更集中在拖拽边
 * @param maxTilt 强度归一化基准，应与 SharedRegistry.tiltMaxValue 保持一致
 * @param pullAmount 沿拖拽方向的最大位移比例
 * @param neckAmount 颈缩强度（垂直于拖拽轴的收拢），0f 关闭
 */
fun genieWarpEffect(
    rotationX: Float,
    rotationY: Float,
    width: Float,
    height: Float,
    isHorizontal: Boolean? = null,
    warpCurve: Float = 0.5f,
    maxTilt: Float = 20f,
    pullAmount: Float = 0.14f,
    neckAmount: Float = 0.7f,
    neckBias: Float = 3f,   // 颈缩焦点偏移系数：0 始终居中（对称），1 最大偏向拖拽侧（差距最大）
): RenderEffect {
    val shader = RuntimeShader(GENIE_WARP_AGSL.trimIndent())

    val dirX = if (rotationY >= 0f) 1f else -1f
    val dirY = if (rotationX >= 0f) -1f else 1f

    val base = if (maxTilt > 0f) maxTilt else 1f
    val magX = (abs(rotationY) / base).coerceIn(0f, 1f)
    val magY = (abs(rotationX) / base).coerceIn(0f, 1f)

    val axisMode = when (isHorizontal) {
        true -> 2f   // 只做纵向：左右边直，上下边弯
        false -> 1f  // 只做横向：上下边直，左右边弯
        null -> 0f
    }

    shader.setFloatUniform("size", width, height)
    shader.setFloatUniform("dir", dirX, dirY)
    shader.setFloatUniform("mag", magX, magY)
    shader.setFloatUniform("warpCurve", warpCurve.coerceAtLeast(0.001f))
    shader.setFloatUniform("neckBias", neckBias)
    shader.setFloatUniform("axisMode", axisMode)
    shader.setFloatUniform("pullAmount", pullAmount)
    shader.setFloatUniform("neckAmount", neckAmount)

    return RuntimeShaderEffect(shader, "content")
}

private const val GENIE_WARP_AGSL = """
uniform shader content;
uniform float2 size;
uniform float2 dir;       // 拖拽方向分量，各为 +1 / -1（无对应轴分量时由 axisMode 控制）
uniform float2 mag;       // 各轴形变强度 0~1
uniform float warpCurve;
uniform float axisMode;   // 0 双轴，1 只横向（禁纵向位移），2 只纵向（禁横向位移）
uniform float pullAmount;
uniform float neckAmount;
uniform float neckBias;   // 颈缩焦点偏移系数 0~1：0=始终居中对称，1=最大偏向拖拽侧

/**
 * 沿拖拽方向的归一化权重：
 *   拖拽边 = 1，对边 = 0（对边天然钉住，不用额外锚点）
 */
float dragWeight(float t, float d) {
    return d >= 0.0 ? t : 1.0 - t;
}

float2 warpDisplacement(float2 p) {
    float2 t = p / size;
    float curve = max(warpCurve, 0.001);

    // gy：沿纵向拖拽方向的权重（拖拽边=1，对边=0）
    // gx：沿横向拖拽方向的权重（拖拽边=1，对边=0）
    float gx = pow(clamp(dragWeight(t.x, dir.x), 0.0, 1.0), curve);
    float gy = pow(clamp(dragWeight(t.y, dir.y), 0.0, 1.0), curve);

    // ── 主位移（沿拖拽轴）──
    // disp.y 只含 gy（纯 y 函数）→ 同一列各行不同，上下边弯；左右边各行同步 → 竖边仍直
    // disp.x 只含 gx（纯 x 函数）→ 同一行各列不同，左右边弯；上下边各列同步 → 横边仍直
    // 斜向时两者叠加
    float2 disp = float2(
        dir.x * mag.x * size.x * pullAmount * gx,
        dir.y * mag.y * size.y * pullAmount * gy
    );

    // ── 颈缩（垂直于拖拽轴的收拢）──
    // 纵向拖拽的颈缩：disp.x 随 y 变化 → 使上下边（y=0/H）产生 x 方向弯曲
    // neckFocusX 由 dir.x 和 mag.x 动态决定：
    //   dir.x == 0（容器水平居中）→ focusX = 0.5，左右对称
    //   dir.x != 0（容器偏左/偏右）→ focusX 偏向拖拽侧，偏移量随 mag.x 增大
    //   效果：容器偏左时右侧颈缩更多，偏右时左侧颈缩更多，中央时完全对称
    float neckFocusX = dir.x > 0.0 ? 0.5 + neckBias * 0.5 * mag.x : (dir.x < 0.0 ? 0.5 - neckBias * 0.5 * mag.x : 0.5);
    float neckFocusY = dir.y > 0.0 ? 0.5 + neckBias * 0.5 * mag.y : (dir.y < 0.0 ? 0.5 - neckBias * 0.5 * mag.y : 0.5);
    disp.x += -(t.x - neckFocusX) * size.x * neckAmount * mag.y * gy;
    disp.y += -(t.y - neckFocusY) * size.y * neckAmount * mag.x * gx;

    if (axisMode == 1.0) {
        // 只做横向：禁止纵向位移，上下边保持直线
        disp.y = 0.0;
    } else if (axisMode == 2.0) {
        // 只做纵向：禁止横向位移，左右边保持直线
        disp.x = 0.0;
    }

    return disp;
}

half4 main(float2 fragCoord) {
    float2 p = fragCoord;
    p = fragCoord - warpDisplacement(p);
    p = fragCoord - warpDisplacement(p);
    p = fragCoord - warpDisplacement(p);

    if (p.x < 0.0 || p.x > size.x || p.y < 0.0 || p.y > size.y) {
        return half4(0.0, 0.0, 0.0, 0.0);
    }

    float2 edge = min(p, size - p);
    float feather = clamp(min(edge.x, edge.y), 0.0, 1.0);

    return content.eval(p) * feather;
}
"""
