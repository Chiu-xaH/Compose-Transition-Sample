package com.xah.transition.ui.component

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import com.xah.transition.ui.NavStackState
import com.xah.transition.ui.model.BackStackEntry
import com.xah.transition.ui.model.NavCommand
import com.xah.transition.ui.model.NavPhase
import com.xah.transition.ui.model.UnderPageVisualEffect
import com.xah.transition.ui.state.LocalNavStackState
import com.xah.transition.ui.style.scaleMirror
import kotlin.coroutines.cancellation.CancellationException

private fun <T> transition() :  SpringSpec<T> = spring(
    dampingRatio = 1f,
    stiffness = 200f,
)

@Composable
fun TransitionNavHost(
    state: NavStackState,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalNavStackState provides state) {
        // 普通返回
        BackHandler(enabled = state.stack.size > 1) {
            state.navigate(NavCommand.Pop)
        }
        // 预测式返回
        PredictiveBackHandler(enabled = state.stack.size > 1) { progress ->
            state.beginPredictivePop()
            try {
                progress.collect { event ->
                    state.updatePredictiveProgress(event.progress)
                }
                state.commitPredictivePop()
            } catch (_: CancellationException) {
                state.cancelPredictivePop()
            }
        }
        // 容器
        Box(modifier) {
            val stack = state.stack
            val top = stack.lastOrNull()
            val under = stack.getOrNull(stack.lastIndex - 1)

            val isCovered = stack.size > 1 && !state.isPopping

            val coveredTransition = updateTransition(
                targetState = isCovered,
                label = "UnderCovered"
            )

            val predictiveProgress = state.predictiveProgress
            val isInPredictive = predictiveProgress != 0f

            // 正常情况返回时
            val backgroundDuration by coveredTransition.animateFloat(
                label = "backgroundDuration",
                transitionSpec = { transition() }
            ) { covered ->
                if (covered) {
                    1f
                } else {
                    0f
                }
            }

            // 背景效果（进入时：Background -> Full，退出时：Full -> Background）
            val underEffect = UnderPageVisualEffect(
                scale = lerp(UnderPageVisualEffect.Full.scale, UnderPageVisualEffect.Background.scale, backgroundDuration),
                blur = lerp(UnderPageVisualEffect.Full.blur, UnderPageVisualEffect.Background.blur, backgroundDuration),
                mask = lerp(UnderPageVisualEffect.Full.mask, UnderPageVisualEffect.Background.mask, backgroundDuration),
                alpha = lerp(UnderPageVisualEffect.Full.alpha, UnderPageVisualEffect.Background.alpha, backgroundDuration),
                corner = lerp(UnderPageVisualEffect.Full.corner, UnderPageVisualEffect.Background.corner, backgroundDuration)
            )

            // 主体效果（进入时：None -> Full，退出时：Full -> None）
            val topEffect = UnderPageVisualEffect(
                scale = lerp(UnderPageVisualEffect.None.scale, UnderPageVisualEffect.Full.scale, backgroundDuration),
                blur = lerp(UnderPageVisualEffect.None.blur, UnderPageVisualEffect.Full.blur, backgroundDuration),
                mask = lerp(UnderPageVisualEffect.None.mask, UnderPageVisualEffect.Full.mask, backgroundDuration),
                alpha = lerp(UnderPageVisualEffect.None.alpha, UnderPageVisualEffect.Full.alpha, backgroundDuration),
                corner = lerp(UnderPageVisualEffect.None.corner, UnderPageVisualEffect.Full.corner, backgroundDuration)
            )

            // 预测式返回时的背景和前景过渡（背景：Background -> PredictiveBackground，前景：Full -> PredictiveSelf）
            val predictiveUnderEffect = UnderPageVisualEffect(
                scale = lerp(UnderPageVisualEffect.Background.scale, UnderPageVisualEffect.PredictiveBackground.scale, state.predictiveProgress),
                blur = lerp(UnderPageVisualEffect.Background.blur, UnderPageVisualEffect.PredictiveBackground.blur, state.predictiveProgress),
                mask = lerp(UnderPageVisualEffect.Background.mask, UnderPageVisualEffect.PredictiveBackground.mask, state.predictiveProgress),
                alpha = lerp(UnderPageVisualEffect.Background.alpha, UnderPageVisualEffect.PredictiveBackground.alpha, state.predictiveProgress),
                corner = lerp(UnderPageVisualEffect.Background.corner, UnderPageVisualEffect.PredictiveBackground.corner, state.predictiveProgress)
            )

            val predictiveTopEffect = UnderPageVisualEffect(
                scale = lerp(UnderPageVisualEffect.Full.scale, UnderPageVisualEffect.PredictiveSelf.scale, state.predictiveProgress),
                blur = lerp(UnderPageVisualEffect.Full.blur, UnderPageVisualEffect.PredictiveSelf.blur, state.predictiveProgress),
                mask = lerp(UnderPageVisualEffect.Full.mask, UnderPageVisualEffect.PredictiveSelf.mask, state.predictiveProgress),
                alpha = lerp(UnderPageVisualEffect.Full.alpha, UnderPageVisualEffect.PredictiveSelf.alpha, state.predictiveProgress),
                corner = lerp(UnderPageVisualEffect.Full.corner, UnderPageVisualEffect.PredictiveSelf.corner, state.predictiveProgress)
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
                        effect =
                            if(isInPredictive) predictiveUnderEffect
                            else if(!isCovered) topEffect
                            else underEffect
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
                        effect =
                            // 没被覆盖，此页面还在显示，不能有enterTopEffect特效否则被缩小了
                            if(under == null) underEffect
                            else if(isInPredictive) predictiveTopEffect
                            else topEffect
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
    LaunchedEffect(effect) {
        if(isUnder == false) {
            return@LaunchedEffect
        }
        Log.d("isUnder $isUnder","topEffect=$effect")

    }
    // 状态闭环
    LaunchedEffect(transition.currentState, transition.targetState) {
        if (!transition.isRunning && transition.currentState == transition.targetState) {
            when (transition.currentState) {
                NavPhase.Entering -> entry.transitionState.targetState = NavPhase.Active
                NavPhase.Exiting -> state.commitPop(entry.id)
                else -> Unit
            }
        }
    }

    Box(
        Modifier
            .fillMaxSize()
            // 遮罩
            .drawWithCache {
                onDrawWithContent {
                    drawContent()
                    if (effect.mask > 0f) {
                        drawRect(Color.Black.copy(alpha = effect.mask))
                    }
                }
            }
            // 模糊
            .blur(effect.blur)
            // 缩放
            .let {
                if (isUnder) {
                    // 背景镜像填充
                    it.scaleMirror(effect.scale)
                } else
                // 主体大小缩放
                    it.graphicsLayer {
                        scaleX = effect.scale
                        scaleY = effect.scale
                    }
            }
            // 圆角
            .clip(RoundedCornerShape(effect.corner))
            // 透明度
            .graphicsLayer {
                alpha = effect.alpha
            }
    ) {
        entry.destination.Content()
    }
}
