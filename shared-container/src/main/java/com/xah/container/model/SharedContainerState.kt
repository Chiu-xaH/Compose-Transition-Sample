package com.xah.container.model

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.unit.dp
import com.sharednav.common.helper.ScreenCornerHelper

class SharedContainerState(
    val key : String
) {
    // 容器Rect
    var containerRect : Rect? = null
    // 内容Rect
    var contentRect : Rect? = null

    // 内容布局
    var containerLayer : GraphicsLayer? = null
    var containerLayerForPixel : GraphicsLayer? = null
    var contentLayer : GraphicsLayer? = null

    // 容器圆角
    var containerCorner: CornerBasedShape = RoundedCornerShape(0.dp)
    var contentCorner: CornerBasedShape = RoundedCornerShape(ScreenCornerHelper.corner)
    // 容器填充策略
    var containerFilledStrategy : ContainerFilledStrategy = ContainerFilledStrategy.Pixel()

    val animation = Animatable(0f)
    // 当前所处状态
    var currentState by mutableStateOf(StatePause.CONTAINER)

    var isFullScreen = true
}