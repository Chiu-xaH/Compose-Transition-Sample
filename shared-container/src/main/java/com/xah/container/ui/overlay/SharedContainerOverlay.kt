package com.xah.container.ui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.lerp
import com.xah.common.util.ScreenCornerHelper
import com.xah.container.logic.model.ShardContainerAction
import com.xah.container.ui.util.LocalSharedContainerRegistry
import kotlin.math.roundToInt

@Composable
fun SharedContainerOverlay() {
    val registry = LocalSharedContainerRegistry.current
    val density = LocalDensity.current

    registry.runningStates.forEach { state ->
        val progress = state.animation.value

        if (state.rectContainer != null && state.rectContent != null) {
            val container = state.rectContainer!!
            val content = state.rectContent!!

            val safelyProgress =  (progress*registry.speedUpRadio).coerceIn(0f,1f)

            val left = lerp(container.left, content.left, progress)
            val top = lerp(container.top, content.top, progress)
            val width = lerp(container.width, content.width, progress)
            val height = lerp(container.height, content.height, progress)

            val contentAlpha = when(state.action) {
                ShardContainerAction.POP -> {
                    lerp(1f,0f,safelyProgress)
                }
                ShardContainerAction.PUSH -> {
                    lerp(0f,1f,safelyProgress)
                }
                ShardContainerAction.NONE -> {
                    1f
                }
            }

            val targetRect = when(state.action) {
                ShardContainerAction.POP -> {
                    content
                }
                ShardContainerAction.PUSH -> {
                    container
                }
                ShardContainerAction.NONE -> {
                    null
                }
            }

            val cornerContent = ScreenCornerHelper.corner

            val corner =  when(state.action) {
                ShardContainerAction.POP -> {
                    androidx.compose.ui.unit.lerp(cornerContent,state.cornerContainer,safelyProgress)
                }
                ShardContainerAction.PUSH -> {
                    androidx.compose.ui.unit.lerp(state.cornerContainer, cornerContent, safelyProgress)
                }
                ShardContainerAction.NONE -> {
                    state.cornerContainer
                }
            }

            Box(
                modifier = Modifier
                    .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                    .size(
                        with(density) { width.toDp() },
                        with(density) { height.toDp() }
                    )
                    .clip(RoundedCornerShape(corner))
            ) {
                // 容器及其颜色填充
                Box(
                    modifier = Modifier.background(state.fillColor)
                ) {
//                    state.contentContainer?.let { it() }
                    // content.width * scale = lerp.width
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Box(modifier = Modifier.align(Alignment.TopCenter)) {
                            Box(
                                modifier = Modifier.graphicsLayer {
                                    val scale = targetRect?.width?.let { width / it }  ?: 0f
                                    scaleX = scale
                                    scaleY = scale
                                    transformOrigin = TransformOrigin(0.5f, 0f)
                                }
                            ) {
                                state.contentContainer?.let { it() }
                            }
                        }
                    }
                }

                // 内容始终透明度淡入淡出
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = contentAlpha
                        }
                ) {
                    state.contentContent?.let { it() }
                }
            }
        }
    }
}
