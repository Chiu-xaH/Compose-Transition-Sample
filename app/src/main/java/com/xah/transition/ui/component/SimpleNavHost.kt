package com.xah.transition.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import com.xah.transition.ui.NavStackState
import com.xah.transition.ui.model.BackStackEntry
import com.xah.transition.ui.model.NavCommand
import com.xah.transition.ui.model.NavPhase
import com.xah.transition.ui.model.UnderPageVisualEffect
import com.xah.transition.ui.state.LocalNavStackState
import com.xah.transition.ui.style.scaleMirror

@Composable
fun SimpleNavHost(
    state: NavStackState,
    modifier: Modifier = Modifier
) {
    // 普通返回
    BackHandler(enabled = state.stack.size > 1) {
        state.navigate(NavCommand.Pop)
    }

    // 预测式返回
//    PredictiveBackHandler(enabled = state.stack.size > 1) { progress ->
//        state.beginPredictivePop()
//        try {
//            progress.collect { event ->
//                state.updatePredictiveProgress(event.progress)
//            }
//            state.commitPredictivePop()
//        } catch (_: CancellationException) {
//            state.cancelPredictivePop()
//        }
//    }

    CompositionLocalProvider(LocalNavStackState provides state) {
        Box(modifier) {

            val stack = state.stack
            val top = stack.lastOrNull()
            val under = stack.getOrNull(stack.lastIndex - 1)

            /* ----------------------------
             * 下层是否被覆盖（普通导航）
             * ---------------------------- */
            val coveredTransition = updateTransition(
                targetState = stack.size > 1,
                label = "UnderCovered"
            )

            /* ----------------------------
             * 普通动画值
             * ---------------------------- */
            val baseScale by coveredTransition.animateFloat(
                label = "baseScale",
                transitionSpec = { spring() }
            ) { covered -> if (covered) 0.9f else 1f }

            val baseBlur by coveredTransition.animateDp(
                label = "baseBlur",
                transitionSpec = { spring() }
            ) { covered -> if (covered) 16.dp else 0.dp }

            val baseDim by coveredTransition.animateFloat(
                label = "baseDim",
                transitionSpec = { spring() }
            ) { covered -> if (covered) 0.35f else 0f }

            /* ----------------------------
             * 预测式叠加（关键）
             * ---------------------------- */
            val p = state.predictiveProgress

            val underEffect = UnderPageVisualEffect(
                scale = lerp(baseScale, 1f, p),
                blur = lerp(baseBlur, 0.dp, p),
                dim = lerp(baseDim, 0f, p)
            )

            // 下层（背景）
            if (under != null) {
                key(under.id) {
                    PageContainer(
                        entry = under,
                        transition = rememberTransition(
                            under.transitionState,
                            under.id
                        ),
                        isUnder = true,
                        effect = underEffect
                    )
                }
            }

            // 上层（前景）
            if (top != null) {
                key(top.id) {
                    PageContainer(
                        entry = top,
                        transition = rememberTransition(
                            top.transitionState,
                            top.id
                        ),
                        isUnder = false,
                        effect = UnderPageVisualEffect.None
                    )
                }
            }
        }
    }
}

@Composable
private fun PageContainer(
    entry: BackStackEntry,
    transition: Transition<NavPhase>,
    isUnder: Boolean,
    effect: UnderPageVisualEffect
) {
    val state = LocalNavStackState.current

    // 上层页面动画
    val selfScale by transition.animateFloat(
        label = "selfScale",
        transitionSpec = { spring() }
    ) { phase ->
        if (isUnder) 1f else when (phase) {
            NavPhase.Entering -> 0f
            NavPhase.Active -> 1f
            else -> 1f
        }
    }

    // 状态闭环
    LaunchedEffect(transition.currentState, transition.targetState) {
        if (!transition.isRunning &&
            transition.currentState == transition.targetState
        ) {
            when (transition.currentState) {
                NavPhase.Entering ->
                    entry.transitionState.targetState = NavPhase.Active
                NavPhase.Exiting ->
                    state.commitPop(entry.id)
                else -> Unit
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            .let {
                if (isUnder) {
                    it.scaleMirror(effect.scale)
                } else
                    it
            }
            .blur(if (isUnder) effect.blur else 0.dp)
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    if (isUnder && effect.dim > 0f) {
                        drawRect(Color.Black.copy(alpha = effect.dim))
                    }
                }
            }
            .let {
                if(!isUnder) {
                    it.graphicsLayer {
                        scaleX = selfScale
                        scaleY = selfScale
                    }
                } else {
                    it
                }
            }
    ) {
        entry.destination.Content()
    }
}

