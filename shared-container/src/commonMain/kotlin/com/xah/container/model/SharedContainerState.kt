package com.xah.container.model

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.layer.GraphicsLayer
import com.sharednav.common.helper.ScreenCornerHelper
import com.sharednav.common.helper.NoneRoundShape

class SharedContainerState(
    val key : String
) {
    // 容器Rect
    var containerRect by mutableStateOf<Rect?>(null)
    // 内容Rect
    var contentRect by mutableStateOf<Rect?>(null)

    // 内容布局
    var containerLayer : GraphicsLayer? = null
    var containerLayerForPixel : GraphicsLayer? = null
    var contentLayer : GraphicsLayer? = null

    // 容器圆角
    var containerCorner: CornerBasedShape = NoneRoundShape
    var contentCorner: CornerBasedShape = RoundedCornerShape(ScreenCornerHelper.corner)
    // 形变时插入一个中间态（圆形）
    var enableQuadraticCorner = false
    // 容器填充策略
    var containerFilledStrategy : ContainerFilledStrategy = ContainerFilledStrategy.Pixel()
    var contentStrategy : ContentStrategy = ContentStrategy.Navigation

    val animation = Animatable(0f)
    // 当前所处状态
    var currentState by mutableStateOf(StatePause.CONTAINER)

    // 被标注为不活跃的将会在合适的时机解除注册 引用计数法
    var isActive : Int = 0
    fun isInActive() = isActive <= 0
    // 跟手Offset
    var contentOffset by mutableStateOf(Offset.Zero)
}