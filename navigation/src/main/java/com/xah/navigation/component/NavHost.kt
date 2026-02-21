package com.xah.navigation.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.util.lerp
import com.xah.navigation.model.Destination
import com.xah.navigation.model.NavActionType
import com.xah.navigation.effect.PageEffect
import com.xah.navigation.util.LocalNavStackState
import com.xah.navigation.state.NavStackState
import com.xah.navigation.util.scaleMirror
import androidx.compose.ui.unit.lerp

private fun <T> transition() :  SpringSpec<T> = spring(
    dampingRatio = 1f,
    stiffness = 50f,
)

@Composable
fun NavHost(
    startDestination: Destination,
    modifier: Modifier = Modifier,
    customBackHandler: (@Composable () -> Unit)? = null,
) {
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
        val progress = remember { Animatable(1f) }
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
                animationSpec = tween<Float>(500)
//                    transition()
            )

            navState.onTransitionFinished()

            // 结束后归位
            progress.snapTo(when (transition.type) {
                NavActionType.PUSH -> 1f
                NavActionType.POP -> 0f
            })
        }

        val visibleEntries =
            transition?.let { listOf(it.from, it.to) }
                ?: listOf(navState.stack.last())

        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(modifier)
        ) {

            visibleEntries.forEach { entry ->
                key(entry.id) {
                    saveableStateHolder.SaveableStateProvider(entry.id) {

                        val isFrom = transition?.from == entry
                        val isTo = transition?.to == entry

                        val animatedProgress = progress.value
                        val underEffect = remember(animatedProgress) { BackgroundEffect(animatedProgress) }

                        Box(
                            Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    if (transition != null) {
                                        when (transition.type) {
                                            NavActionType.PUSH -> {
                                                if (isFrom) { }
                                                if(isTo) {
                                                    // 简单变大动画
//                                                    scaleX = animatedProgress
//                                                    scaleY = animatedProgress
//                                                    transformOrigin = TransformOrigin(0.5f,0.3f)
                                                }
                                            }
                                            NavActionType.POP -> {
                                                if(isFrom) {
                                                    // 简单变小动画
                                                    alpha = 0f
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
                                                    // 渐变缩放模糊
                                                    return@let with(underEffect) {
                                                        it.effect()
                                                    }
                                                }
                                            }
                                            NavActionType.POP -> {
                                                if (isTo) {
                                                    // 渐变缩放模糊
                                                    return@let with(underEffect) {
                                                        it.effect()
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    return@let it
                                }
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

    fun Modifier.effect() : Modifier {
        return this.mask().blur().scale()
    }
}
