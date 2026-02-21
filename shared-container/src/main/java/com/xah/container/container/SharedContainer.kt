package com.xah.container.container

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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xah.container.model.ContainerFilledStrategy
import com.xah.container.util.LocalSharedContainerRegistry

private fun Modifier.sharedContainer(
    key : Any,
    containerFilledStrategy : ContainerFilledStrategy,
    corner : Dp,
    content : @Composable () -> Unit
): Modifier = composed {
    val registry = LocalSharedContainerRegistry.current
    if(!registry.enabled) {
        return@composed this
    }
    val state = remember { registry.getOrCreate(key) }

    LaunchedEffect(containerFilledStrategy,corner) {
        state.containerLayout = content
        state.containerFilledStrategy = containerFilledStrategy
        state.containerCorner = corner
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

            state.containerRect = Rect(
                left = position.x,
                top = position.y,
                right = position.x + size.width,
                bottom = position.y + size.height
            )
        }
}

private fun Modifier.sharedContent(
    key : Any,
    content : @Composable () -> Unit
): Modifier = composed {
    val registry = LocalSharedContainerRegistry.current
    if(!registry.enabled) {
        return@composed this
    }
    val state = remember { registry.getOrCreate(key) }
    LaunchedEffect(Unit) {
        state.contentLayout = content
    }

    this
        .drawWithContent {
            // 隐藏原组件
            if (!state.isRunning) {
                drawContent()
            }
        }
        // 记录组件的位置、大小
        .onGloballyPositioned { coordinates ->
            val position = coordinates.positionInRoot()
            val size = coordinates.size

            state.contentRect = Rect(
                left = position.x,
                top = position.y,
                right = position.x + size.width,
                bottom = position.y + size.height
            )
        }
}

/**
 * 共享容器的内容
 * @param key 两个容器之间的Key
 */
@Composable
fun SharedContent(
    key : Any,
    modifier : Modifier = Modifier,
    content : @Composable () -> Unit
)  {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier.sharedContent(key,content)
        ) {
            content()
        }
    }
}

/** 共享容器的容器
 * @param key 两个容器之间的Key
 * @param containerFilledStrategy 容器填充策略
 * @param corner 容器圆角
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