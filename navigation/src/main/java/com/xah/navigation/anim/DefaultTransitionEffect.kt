package com.xah.navigation.anim

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.tween
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.model.anim.PageEffects
import com.xah.navigation.model.anim.TransitionEffect


/**
 * 从四周滑入
 * eg: 微信支付完成弹窗
 * PUSH -> 起始界面压暗，目标界面从底部推入直到完全覆盖
 * POP -> 方向反向，其余不变
 *
 * 参数：压暗程度
 * 预测式返回手势阈值：0.875f
 */
data class SlideTransitionEffect(
    val direction: Direction = Direction.BOTTOM,
    override val pageEffect : PageEffects = SlidePageEffects(direction),
    override val predictiveMinValue: Float = NavigationController.DEFAULT_SHARED_MAX_PRECENT,
    override val pushAnimation: AnimationSpec<Float> = tween(400, easing = NavigationController.DEFAULT_EASING),
    override val popAnimation: AnimationSpec<Float> = tween(400, easing = NavigationController.DEFAULT_EASING)
) : TransitionEffect

/**
 * 从右侧滑入
 * eg: 设置的多级界面
 * PUSH -> 起始界面位移向左1/3并压暗，目标界面从右侧推入直到完全覆盖
 * POP -> 方向反向，其余不变
 *
 * 参数：位移程度、压暗程度
 * 预测式返回手势阈值：0.8f
 */
data class FlipTransitionEffect(
    override val pageEffect : PageEffects = FlipPageEffects(),
    override val predictiveMinValue: Float = 0.8f,
    override val pushAnimation: AnimationSpec<Float> = tween(450, easing = NavigationController.DEFAULT_EASING),
    override val popAnimation: AnimationSpec<Float> = tween(450, easing = NavigationController.DEFAULT_EASING)
) : TransitionEffect

/**
 * 缩放
 * eg: 桌面动效
 * 起始界面缩小到一定阈值0.875f并模糊、缩放、压暗，目标界面从屏幕中央偏上位置开始缩放变大直到完全覆盖
 *
 * 参数：若干
 * 预测式返回手势阈值：0.875f
 */
data class DefaultTransitionEffect(
    override val pageEffect : PageEffects = DefaultPageEffects(),
    override val predictiveMinValue: Float = NavigationController.DEFAULT_SHARED_MAX_PRECENT,
    override val pushAnimation: AnimationSpec<Float> = tween(NavigationController.DEFAULT_SHARED_SPEC*6/5, easing = NavigationController.DEFAULT_EASING),
    override val popAnimation: AnimationSpec<Float> = tween(NavigationController.DEFAULT_SHARED_SPEC*6/5, easing = NavigationController.DEFAULT_EASING)
) : TransitionEffect

/**
 * 跳转
 * eg: 手机系统中的应用跳转
 * PUSH -> 起始界面缩小并位移向左，目标界面从右侧放大并位移向右，两者缩放统一，背景为纯黑色
 * POP -> 方向反向，其余不变
 *
 * 参数：缩放程度、位移程度
 * 预测式返回手势阈值：0.5f
 */
data class JumpTransitionEffect(
    override val pageEffect : PageEffects = JumpPageEffects(),
    override val predictiveMinValue: Float = 0.5f,
    override val pushAnimation: AnimationSpec<Float> = tween(450, easing = NavigationController.DEFAULT_EASING),
    override val popAnimation: AnimationSpec<Float> = tween(450, easing = NavigationController.DEFAULT_EASING)
) : TransitionEffect
