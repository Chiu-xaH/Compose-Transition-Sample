package com.xah.container.model

/**
 * 形变效果
 */
enum class TiltEffect {
    ROTATION, // 倾斜
    SHADER_2, // 非线性扭曲 iOS26 单向 开发测试中
    SHADER_4, // 非线性扭曲 iOS26 双向 开发测试中
    NONE // 无
}