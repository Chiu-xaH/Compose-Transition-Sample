package com.xah.container.ui.container

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xah.container.logic.ContainerFilledStrategy
import com.xah.container.logic.model.SharedContainerState
import com.xah.container.ui.util.LocalSharedContainerRegistry

private fun Modifier.sharedContainer(
    key : Any,
    containerFilledStrategy : ContainerFilledStrategy?,
    corner : Dp?,
    content : @Composable () -> Unit
): Modifier = composed {
    val registry = LocalSharedContainerRegistry.current
    if(!registry.enabled) {
        return@composed this
    }
    val state = remember { registry.getOrCreate(key) }

    LaunchedEffect(containerFilledStrategy,corner) {
        state.layout = content
        if (containerFilledStrategy != null) {
            state.containerFilledStrategy = containerFilledStrategy
        }
        if (corner != null) {
            state.containerCorner = corner
        }
    }

    this
        .drawWithContent {
            // 隐藏原组件
            if (!state.isRunning) {
                drawContent()
            }
        }
        // 记录两个组件的位置、大小
        .onGloballyPositioned { coordinates ->
            val position = coordinates.positionInRoot()
            val size = coordinates.size

            state.layoutRect = Rect(
                left = position.x,
                top = position.y,
                right = position.x + size.width,
                bottom = position.y + size.height
            )
        }
}

/**
 * 共享容器的内容
 */
@Composable
fun SharedContent(
    key : Any,
    modifier : Modifier = Modifier,
    content : @Composable () -> Unit
)  {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier.sharedContainer(key,null,null,content)
        ) {
            content()
        }
    }
}

/** 共享容器的容器
 * @param key 两个容器之间的Key
 * @param fillColor sdk33以上优先使用底部1像素提取填充,无需传入颜色
 */
@Composable
fun SharedContainer(
    key : Any,
    modifier : Modifier = Modifier,
    containerFilledStrategy : ContainerFilledStrategy = ContainerFilledStrategy.Pixel(),
    corner : Dp = 0.dp,
    content : @Composable () -> Unit
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .sharedContainer(key,containerFilledStrategy,corner,content)
                .clip(RoundedCornerShape(corner))
        ) {
            content()
        }
    }
}