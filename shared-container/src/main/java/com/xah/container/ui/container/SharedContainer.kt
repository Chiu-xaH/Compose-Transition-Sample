package com.xah.container.ui.container

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import com.xah.container.ui.util.LocalSharedContainerRegistry

private fun Modifier.sharedContainer(
    key : Any,
): Modifier = composed {
    val registry = LocalSharedContainerRegistry.current
    val state = registry.getOrCreate(key)

    this
        .drawWithContent {
            // 隐藏原组件
            if (!state.isRunning) {
                drawContent()
                // 记录两个组件的快照
//                state.layoutLayer?.record {
//                    val bounds = state.layoutRect ?: return@record
//                    withTransform({
//                        clipRect(0f, 0f, bounds.width, bounds.height)
//                    }) {
//                        this@drawWithContent.drawContent()
//                    }
//                }
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
        Box(modifier = Modifier.sharedContainer(key)) {
            content()
        }
    }
}
