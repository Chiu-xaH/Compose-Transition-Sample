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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import com.xah.container.ui.util.LocalSharedContainerRegistry

private fun Modifier.sharedContainer(
    key : Any,
    fillColor : Color? = null,
    corner : Dp? = null,
    content : @Composable () -> Unit
): Modifier = composed {
    val registry = LocalSharedContainerRegistry.current
    val state = remember { registry.getOrCreate(key) }

    LaunchedEffect(fillColor,corner,state.action) {
        state.content = content
        if (fillColor != null) {
            state.fillColor = fillColor
        }
        if (corner != null) {
            state.cornerContainer = corner
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
        .onGloballyPositioned {
            state.layoutRect = it.boundsInRoot()
        }
}

/** 共享容器的容器
 */
@Composable
fun SharedContent(
    key : Any,
    modifier : Modifier = Modifier,
    content : @Composable () -> Unit
)  = SharedContainer(
    key,
    modifier,
    null,
    null,
    content
)


/** 共享容器的内容
 * @param key 两个容器之间的Key
 * @param fillColor 两个容器任一方指定颜色即可，不指定直接传null
 */
@Composable
fun SharedContainer(
    key : Any,
    modifier : Modifier = Modifier,
    fillColor : Color? = null,
    corner : Dp? = null,
    content : @Composable () -> Unit
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .sharedContainer(key,fillColor,corner,content)
                .let {
                    corner?.let { size ->
                        it.clip(RoundedCornerShape(size))
                    } ?: it
                }
        ) {
            content()
        }
    }
}
