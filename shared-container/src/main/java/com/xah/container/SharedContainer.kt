package com.xah.container

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned

private fun Modifier.sharedContainer(
    key : Any,
): Modifier = composed {
    val registry = LocalSharedContainerRegistry.current
    val state = registry.getOrCreate(key)
    this
        // 隐藏原组件
        .drawWithContent {
            if (!state.isRunning) drawContent()
        }
        // 记录两个组件的Rect
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
        Box(modifier = Modifier.sharedContainer(key)) {
            content()
        }
    }
}
