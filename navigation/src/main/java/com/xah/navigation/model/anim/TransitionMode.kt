package com.xah.navigation.model.anim

import com.xah.navigation.anim.DefaultPageEffects
import com.xah.navigation.anim.JumpPageEffects
import com.xah.navigation.anim.SlideFromBottomPageEffects
import com.xah.navigation.anim.SlideFromEndPageEffects

interface TransitionMode {
    val pageEffects: PageEffects
}

/**
 * 从底部滑入
 * eg: 微信支付完成弹窗
 * PUSH -> 起始界面压暗，目标界面从底部推入直到完全覆盖
 * POP -> 方向反向，其余不变
 *
 * 参数：压暗程度
 */
data class SlideFromBottom(
    override val pageEffects : PageEffects = SlideFromBottomPageEffects()
) : TransitionMode

/**
 * 从右侧滑入
 * eg: 设置的多级界面
 * PUSH -> 起始界面位移向左1/3并压暗，目标界面从右侧推入直到完全覆盖
 * POP -> 方向反向，其余不变
 *
 * 参数：位移程度、压暗程度
 */
data class SlideFromEnd(
    override val pageEffects : PageEffects = SlideFromEndPageEffects()
) : TransitionMode

/**
 * 缩放
 * eg: 桌面动效
 * 起始界面缩小到一定阈值0.875f并模糊、缩放、压暗，目标界面从屏幕中央偏上位置开始缩放变大直到完全覆盖
 *
 * 参数：若干
 */
data class Scale(
    override val pageEffects : PageEffects = DefaultPageEffects()
) : TransitionMode

/**
 * 跳转
 * eg: 手机系统中的应用跳转
 * PUSH -> 起始界面缩小并位移向左，目标界面从右侧放大并位移向右，两者缩放统一，背景为纯黑色
 * POP -> 方向反向，其余不变
 *
 * 参数：缩放程度、位移程度
 */
data class Jump(
    override val pageEffects : PageEffects = JumpPageEffects()
) : TransitionMode
