package com.xah.floating.overlay

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.sharednav.common.util.disableTouchEvent
import com.xah.floating.util.LocalFloatingController

@Composable
fun FloatingOverlay() {
    val controller = LocalFloatingController.current
    val stack = controller.stack

    Box(modifier = Modifier.fillMaxSize()) {
        stack.forEachIndexed { index, entry ->
            key(entry.id) {
                val isTop = index == stack.lastIndex

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .let {
                            if(!isTop) {
                                it
                            } else {
                                it.disableTouchEvent { controller.pop() }
                            }
                        }
                )
                Box(
                    modifier = Modifier
                        .let {
                            if(isTop) {
                                it
                            } else {
                                it
                                    .disableTouchEvent { controller.pop() }
                                    .graphicsLayer(alpha = 1 - controller.effect.backgroundEffect.pageEffect.mask)
                            }
                        }

                ) {
                    entry.window.Content()
                }
            }
        }
    }
}
