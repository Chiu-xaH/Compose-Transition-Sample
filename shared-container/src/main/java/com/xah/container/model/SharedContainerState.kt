package com.xah.container.model

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class SharedContainerState() {
    // 容器Rect
    var containerRect: Rect? = null
    // 内容Rect
    var contentRect: Rect? = null

    // 容器布局
    var containerLayout: (@Composable () -> Unit)? = null
    // 内容布局
    var contentLayout: (@Composable () -> Unit)? = null

    // 容器圆角
    var containerCorner: Dp = 0.dp
    // 容器填充策略
    var containerFilledStrategy : ContainerFilledStrategy = ContainerFilledStrategy.Pixel()

    // 动画
    val animation = Animatable(0f)
    // 结束开始标志位
    var isRunning by mutableStateOf(false)
}