package com.xah.container.logic.model

import androidx.compose.animation.core.Animatable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class SharedContainerState() {
    // 记录Rect（位置、大小）
    var layoutRect by mutableStateOf<Rect?>(null)
    // 容器
    var rectContainer by mutableStateOf<Rect?>(null)
    // 内容
    var rectContent by mutableStateOf<Rect?>(null)

    // 记录布局内容
    var content : (@Composable () -> Unit)? = null
    // 容器
    var contentContainer : (@Composable () -> Unit)? = null
    // 内容
    var contentContent: (@Composable () -> Unit)? = null

    // 记录圆角
    // 容器
    var cornerContainer : Dp = 0.dp

    // 记录背景色用于填充
    var fillColor : Color = Color.Companion.Black

    val animation = Animatable(0f)
    var isRunning by mutableStateOf(false)
    var action by mutableStateOf(ShardContainerAction.NONE)
}