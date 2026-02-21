package com.xah.navigation.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import com.xah.common.ScreenCornerHelper
import com.xah.common.touchEvent
import com.xah.container.ui.util.LocalSharedContainerRegistry
import com.xah.navigation.effect.PageEffect
import com.xah.navigation.model.Destination
import com.xah.navigation.model.NavActionType
import com.xah.navigation.state.NavStackState
import com.xah.navigation.util.LocalNavStackState
import com.xah.navigation.util.scaleMirror

private const val animationSpecSharedTween = 500
private val animationSpec = tween<Float>(animationSpecSharedTween*8/5)
private val animationSpecWithoutShared = tween<Float>(animationSpecSharedTween*13/10)

@Composable
fun NavHost(
    startDestination: Destination,
    modifier: Modifier = Modifier,
    onAnimatedFinished : (() -> Unit)? = null,
    customBackHandler: (@Composable () -> Unit)? = null,
) {
    val registry = LocalSharedContainerRegistry.current
    val saveableStateHolder = rememberSaveableStateHolder()
    val navState = remember { NavStackState(startDestination) }

    CompositionLocalProvider(
        LocalNavStackState provides navState,
    ) {

        if (customBackHandler == null) {
            BackHandler(enabled = navState.stack.size > 1) {
                navState.pop()
            }
        } else {
            customBackHandler()
        }

        val transition = navState.navTransition
        val progress = navState.transitionProgress
        var tag by remember { mutableStateOf(false) }

        // 当 transition 变化时启动动画
        LaunchedEffect(transition) {
            // 首次初始化
            if(!tag){
                progress.snapTo(0f)
                tag = true
                return@LaunchedEffect
            }

            transition ?: return@LaunchedEffect

            val target = when (transition.type) {
                NavActionType.PUSH -> 1f
                NavActionType.POP -> 0f
            }

            progress.animateTo(
                targetValue = target,
                animationSpec =
                    if(registry.isRunning) {
                        animationSpec
                    } else {
                        animationSpecWithoutShared
                    }
            )

            onAnimatedFinished?.let { it() }
            navState.onTransitionFinished()

            // 结束后归位
            progress.snapTo(when (transition.type) {
                NavActionType.PUSH -> 1f
                NavActionType.POP -> 0f
            })
        }

        val visibleEntries = remember(transition) {
            when (transition?.type) {
                NavActionType.POP -> listOf(transition.to, transition.from)
                NavActionType.PUSH -> listOf(transition.from, transition.to)
                else -> listOf(navState.stack.last())
            }
        }




        Box(modifier = modifier.fillMaxSize()) {
            visibleEntries.forEach { entry ->
                key(entry.id) {
                    saveableStateHolder.SaveableStateProvider(entry.id) {

                        val isFrom = transition?.from == entry
                        val isTo = transition?.to == entry

                        val animatedProgress = progress.value
                        val underEffect = remember(animatedProgress) { BackgroundEffect(animatedProgress) }
                        val upEffect = remember(animatedProgress) { ForegroundEffect(animatedProgress) }
                        val isBackground =  if(transition == null) {
                            false
                        } else {
                            (transition.type == NavActionType.PUSH && isFrom) ||
                                    (transition.type == NavActionType.POP && isTo)
                        }

                        Box(
                            Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    if (transition != null) {
                                        when (transition.type) {
                                            NavActionType.PUSH -> {
                                                if (isFrom) { }
                                                if(isTo) { }
                                            }
                                            NavActionType.POP -> {
                                                if(isFrom) {
                                                    // 有共享元素
                                                    if(registry.isRunning) {
                                                        alpha = 0f
                                                    }
                                                }
                                                if (isTo) { }
                                            }
                                        }
                                    }
                                }
                                .let {
                                    if (transition != null) {
                                        when (transition.type) {
                                            NavActionType.PUSH -> {
                                                if (isFrom) {
                                                    // 背景
                                                    return@let with(underEffect) {
                                                        it.effect()
                                                    }
                                                }
                                                if (isTo) {
                                                    // 目标屏幕
                                                    if(!registry.isRunning) {
                                                        return@let with(upEffect) {
                                                            it.effect()
                                                        }
                                                    }
                                                }
                                            }
                                            NavActionType.POP -> {
                                                if (isTo) {
                                                    // 背景
                                                    return@let with(underEffect) {
                                                        it.effect()
                                                    }
                                                }
                                                if (isFrom) {
                                                    // 退出屏幕
                                                    if(!registry.isRunning) {
                                                        return@let with(upEffect) {
                                                            it.effect()
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    return@let it
                                }
                                // 背景禁用触摸事件
                                .touchEvent(
                                    !isBackground
                                )
                        ) {
                            entry.destination.Content()
                        }
                    }
                }
            }
        }
    }
}

private class BackgroundEffect(animatedProgress : Float) {

    private val effect = PageEffect(
        scale = lerp(
            PageEffect.Full.scale,
            PageEffect.Background.scale,
            animatedProgress
        ),
        blur = lerp(
            PageEffect.Full.blur,
            PageEffect.Background.blur,
            animatedProgress
        ),
        mask = lerp(
            PageEffect.Full.mask,
            PageEffect.Background.mask,
            animatedProgress
        ),
        alpha = lerp(
            PageEffect.Full.alpha,
            PageEffect.Background.alpha,
            animatedProgress
        ),
        corner = lerp(
            PageEffect.Full.corner,
            PageEffect.Background.corner,
            animatedProgress
        )
    )

    private fun Modifier.mask() : Modifier {
        return this.drawWithCache {
            onDrawWithContent {
                drawContent()
                if (effect.mask > 0f) {
                    drawRect(Color.Black.copy(alpha = effect.mask))
                }
            }
        }
    }

    private fun Modifier.blur() : Modifier {
        return this.blur(effect.blur)
    }

    private fun Modifier.scale() : Modifier {
        return this.scaleMirror(effect.scale)
    }

    fun Modifier.effect() : Modifier = this.mask().blur().scale()
}

private class ForegroundEffect(animatedProgress : Float) {

    private val effect = PageEffect(
        scale = lerp(
            PageEffect.None.scale,
            PageEffect.Full.scale,
            animatedProgress
        ),
        blur = lerp(
            PageEffect.None.blur,
            PageEffect.Full.blur,
            animatedProgress
        ),
        mask = lerp(
            PageEffect.None.mask,
            PageEffect.Full.mask,
            animatedProgress
        ),
        alpha = lerp(
            PageEffect.None.alpha,
            PageEffect.Full.alpha,
            animatedProgress
        ),
        corner = lerp(
            ScreenCornerHelper.corner*2,
//            PageEffect.None.corner,
            ScreenCornerHelper.corner,
//            PageEffect.Full.corner,
            animatedProgress
        )
    )


    private fun Modifier.blur() : Modifier {
        return this.blur(effect.blur)
    }
    private fun Modifier.corner() : Modifier {
        return this.clip(RoundedCornerShape(effect.corner))
    }

    private fun Modifier.scale() : Modifier {
        return this.graphicsLayer {
            scaleX = effect.scale
            scaleY = effect.scale
            alpha = effect.alpha
            transformOrigin = TransformOrigin(0.5f,0.3f)
        }
    }

    fun Modifier.effect() : Modifier = this.blur().scale().corner()
}
