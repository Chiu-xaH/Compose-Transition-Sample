package com.xah.container.ui.container

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import com.xah.container.ui.util.LocalSharedContainerRegistry

private fun Modifier.sharedContainer(
    key : Any,
    content : @Composable () -> Unit
): Modifier = composed {
    val registry = LocalSharedContainerRegistry.current
    val state = remember { registry.getOrCreate(key) }
    state.content = content

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

@Composable
fun SharedContainer(
    key : Any,
    modifier : Modifier = Modifier,
    content : @Composable () -> Unit
) {
    Box(modifier = modifier) {
        Box(modifier = Modifier.sharedContainer(key,content)) {
            content()
        }
    }
}
