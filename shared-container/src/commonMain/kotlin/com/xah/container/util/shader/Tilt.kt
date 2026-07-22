package com.xah.container.util.shader

import androidx.compose.ui.graphics.RenderEffect
import com.xah.shader.skia.RuntimeShader
import com.xah.shader.skia.RuntimeShaderEffect

/**
 * 开发中，目前这个效果不对
 * iOS26 非线性扭曲效果
 *
 * @param rotationX X 轴旋转值（来自倾斜计算）
 * @param rotationY Y 轴旋转值（来自倾斜计算）
 * @param warpCurve 扭曲程度
 */
fun genieWarpEffect(
    rotationX: Float,
    rotationY: Float,
    width: Float,
    height: Float,
    isHorizontal : Boolean? = null,
    warpCurve: Float = 0.5f // 1f 线性 <1f 向内弯曲 > 1f 向外弯曲
): RenderEffect {
    val shader = RuntimeShader((
            when(isHorizontal) {
                true -> GENIE_WARP_AGSL_2
                false -> GENIE_WARP_AGSL_1
                null -> GENIE_WARP_AGSL
            }
        ).trimIndent()
    )
    shader.setFloatUniform("size", width, height)
    shader.setFloatUniform("rotationX", rotationX)
    shader.setFloatUniform("rotationY", rotationY)
    shader.setFloatUniform("warpCurve", warpCurve)
    return RuntimeShaderEffect(shader, "content")
}

// 四条边均变
private const val GENIE_WARP_AGSL = """
uniform shader content;
uniform float2 size;
uniform float rotationX;
uniform float rotationY;
uniform float warpCurve;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord;

    // roY 控制横向吸附，roX 控制纵向吸附
    // 正数吸向右/下，负数吸向左/上
    float dirX = rotationY >= 0.0 ? 1.0 : -1.0;
    float dirY = rotationX >= 0.0 ? -1.0 : 1.0;

    float strengthX = clamp(abs(rotationY) / 20.0, 0.0, 0.95);
    float strengthY = clamp(abs(rotationX) / 20.0, 0.0, 0.95);

    // 0~1 坐标
    float x = fragCoord.x / size.x;
    float y = fragCoord.y / size.y;

    // 按方向转成“离吸附边距离”：吸附边为 0，远端为 1
    float ax = dirX > 0.0 ? 1.0 - x : x;
    float ay = dirY > 0.0 ? 1.0 - y : y;

    // 越靠近吸附边，收缩越强；远端几乎不变
    float curve = max(warpCurve, 0.001);
    float pinchX = pow(1.0 - ax, curve);
    float pinchY = pow(1.0 - ay, curve);

    // 横向运动时，压缩垂直方向；纵向运动时，压缩水平方向
    float verticalScale = 1.0 - strengthX * pinchX;
    float horizontalScale = 1.0 - strengthY * pinchY;

    // 反向采样：以吸附边为锚点，避免中心缩放导致宽/高先缩小再恢复
    float anchorX = dirX > 0.0 ? size.x : 0.0;
    float anchorY = dirY > 0.0 ? size.y : 0.0;

    uv.x = anchorX + (fragCoord.x - anchorX) / max(horizontalScale, 0.001);
    uv.y = anchorY + (fragCoord.y - anchorY) / max(verticalScale, 0.001);

    // 额外沿吸附方向做轻微拖拽，增强 Genie 的“被吸走”感
    uv.x -= dirX * strengthX * pinchX * size.x * 0.08;
    uv.y -= dirY * strengthY * pinchY * size.y * 0.08;

    if (uv.x < 0.0 || uv.x > size.x || uv.y < 0.0 || uv.y > size.y) {
        return half4(0.0, 0.0, 0.0, 0.0);
    }

    return content.eval(uv);
}
"""

// 上下边不变
private const val GENIE_WARP_AGSL_1 = """
uniform shader content;
uniform float2 size;
uniform float rotationX;
uniform float rotationY;
uniform float warpCurve;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord;

    // roY 控制横向吸附，roX 控制纵向吸附
    // 正数吸向右/下，负数吸向左/上
    float dirX = rotationY >= 0.0 ? 1.0 : -1.0;
    float dirY = rotationX >= 0.0 ? -1.0 : 1.0;

    float strengthX = clamp(abs(rotationY) / 20.0, 0.0, 0.95);
    float strengthY = clamp(abs(rotationX) / 20.0, 0.0, 0.95);

    // 0~1 坐标
    float x = fragCoord.x / size.x;
    float y = fragCoord.y / size.y;

    // 上下边保持水平：不再重映射 uv.y，只让每一行的 x 映射发生变化
    // rotationY 决定左右吸附方向，rotationX 决定上下哪一侧的形变更强
    float curve = max(warpCurve, 0.001);
    float rowFromDirY = dirY > 0.0 ? y : 1.0 - y;
    float rowFactor = pow(rowFromDirY, curve);

    // 保留原版纵向动势，但转移到 x 方向做补偿，避免只剩横向压缩导致起始回缩
    float ax = dirX > 0.0 ? 1.0 - x : x;
    float pinchX = pow(1.0 - ax, curve);
    float verticalScale = 1.0 - strengthX * pinchX;
    float verticalInfluence = 1.0 - verticalScale;

    // 横向动势提供基础形变，纵向动势按行增强/补偿
    float rowStrength = clamp(strengthY * rowFactor + verticalInfluence * (0.35 + 0.65 * rowFactor), 0.0, 0.95);
    float horizontalScale = 1.0 - rowStrength;

    // 以横向吸附边为锚点做反向采样；每一行同一个 scale，所以水平边不会弯
    float anchorX = dirX > 0.0 ? size.x : 0.0;
    uv.x = anchorX + (fragCoord.x - anchorX) / max(horizontalScale, 0.001);
    uv.y = fragCoord.y;

    // 只做逐行水平拖拽，不碰 y；顶部/底部仍是水平直线，但宽度可以不同
    uv.x -= dirX * rowStrength * size.x * 0.06;

    if (uv.x < 0.0 || uv.x > size.x || uv.y < 0.0 || uv.y > size.y) {
        return half4(0.0, 0.0, 0.0, 0.0);
    }

    return content.eval(uv);
}
"""

// 左右边不变
private const val GENIE_WARP_AGSL_2 = """
uniform shader content;
uniform float2 size;
uniform float rotationX;
uniform float rotationY;
uniform float warpCurve;

half4 main(float2 fragCoord) {
    float2 uv = fragCoord;

    // roY 控制横向吸附，roX 控制纵向吸附
    // 正数吸向右/下，负数吸向左/上
    float dirX = rotationY >= 0.0 ? 1.0 : -1.0;
    float dirY = rotationX >= 0.0 ? -1.0 : 1.0;

    float strengthX = clamp(abs(rotationY) / 20.0, 0.0, 0.95);
    float strengthY = clamp(abs(rotationX) / 20.0, 0.0, 0.95);

    // 0~1 坐标
    float x = fragCoord.x / size.x;
    float y = fragCoord.y / size.y;

    // 左右边保持直线：不再重映射 uv.x，只让每一列的 y 映射发生变化
    // rotationX 决定上下吸附方向，rotationY 决定左右哪一侧的形变更强
    float curve = max(warpCurve, 0.001);
    float columnFromDirX = dirX > 0.0 ? x : 1.0 - x;
    float columnFactor = pow(columnFromDirX, curve);

    // 保留原版横向动势，但转移到 y 方向做补偿，避免只剩纵向压缩导致起始回缩
    float ay = dirY > 0.0 ? 1.0 - y : y;
    float pinchY = pow(1.0 - ay, curve);
    float horizontalScale = 1.0 - strengthY * pinchY;
    float horizontalInfluence = 1.0 - horizontalScale;

    // 纵向动势提供基础形变，横向动势按列增强/补偿
    float columnStrength = clamp(strengthX * columnFactor + horizontalInfluence * (0.35 + 0.65 * columnFactor), 0.0, 0.95);
    float verticalScale = 1.0 - columnStrength;

    // 以纵向吸附边为锚点做反向采样；每一列同一个 scale，所以左右边不会弯
    float anchorY = dirY > 0.0 ? size.y : 0.0;
    uv.x = fragCoord.x;
    uv.y = anchorY + (fragCoord.y - anchorY) / max(verticalScale, 0.001);

    // 只做逐列垂直拖拽，不碰 x；左右边仍是直线，但高度可以不同
    uv.y -= dirY * columnStrength * size.y * 0.06;

    if (uv.x < 0.0 || uv.x > size.x || uv.y < 0.0 || uv.y > size.y) {
        return half4(0.0, 0.0, 0.0, 0.0);
    }

    return content.eval(uv);
}
"""
