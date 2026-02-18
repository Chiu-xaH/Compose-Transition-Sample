package com.xah.container.ui.overlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.util.lerp
import androidx.compose.ui.zIndex
import com.xah.container.logic.SharedContainerRegistry
import com.xah.container.ui.util.LocalSharedContainerRegistry
import kotlin.math.roundToInt


@Composable
fun SharedContainerRoot(
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val registry = remember { SharedContainerRegistry() }
    CompositionLocalProvider(
        LocalSharedContainerRegistry provides registry
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // 界面
            content()

            // Overlay 永远在界面下面
            Box(Modifier.zIndex(-1f)) {
                registry.runningStates.forEach { state ->
                    if (state.rectFrom != null && state.rectTo != null) {

                        val s = state.rectFrom!!
                        val e = state.rectTo!!
                        val p = state.animation.value

                        val left = lerp(s.left, e.left, p)
                        val top = lerp(s.top, e.top, p)
                        val width = lerp(s.width, e.width, p)
                        val height = lerp(s.height, e.height, p)


                        Box(
                            modifier = Modifier
                                .offset { IntOffset(left.roundToInt(), top.roundToInt()) }
                                .size(
                                    with(density) { width.toDp() },
                                    with(density) { height.toDp() }
                                )
                                .background(Color.Black)
//                                .drawWithCache {
//                                    onDrawWithContent {
//                                        state.layoutLayer?.let {
//                                            drawLayer(it)
//                                        }
//                                    }
//                                }
                        )
                    }
                }
            }
        }
    }
}