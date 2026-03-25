package com.xah.floating.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.sharednav.common.modifier.disableTouchEvent
import com.xah.floating.util.LocalFloatingController

@Composable
fun FloatingOverlay() {
    val controller = LocalFloatingController.current
    val stack = controller.stack
    val foregroundEffect = controller.effect.foregroundEffect

    Box(modifier = Modifier.fillMaxSize()) {
        stack.forEachIndexed { index, entry ->
            key(entry.id) {
                val isTop = index == stack.lastIndex

                // 初始 visible = false，随后立即设为 true，触发入场动画
                val visibleState = remember {
                    MutableTransitionState(initialState = false).apply {
                        targetState = true
                    }
                }

                // 注册到 controller，使 pop() 可以控制退场动画
                DisposableEffect(entry.id) {
                    controller.registerVisibleState(entry.id, visibleState)
                    onDispose {
                        controller.unregisterVisibleState(entry.id)
                    }
                }

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
                AnimatedVisibility(
                    visibleState = visibleState,
                    enter = foregroundEffect.enter,
                    exit = foregroundEffect.exit,
                    modifier = Modifier
                        .let {
                            if (isTop) {
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
