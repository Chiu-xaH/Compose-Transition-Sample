package com.xah.navigation.component

import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.ExperimentalAnimationApi
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import com.xah.navigation.model.BackStackEntry
import com.xah.navigation.model.NavActionState
import com.xah.navigation.model.NavCommand
import com.xah.navigation.model.NavPhase
import com.xah.navigation.model.UnderPageVisualEffect
import com.xah.navigation.state.LocalNavStackState
import com.xah.navigation.state.NavStackState
import com.xah.navigation.style.scaleMirror
import kotlin.coroutines.cancellation.CancellationException

private fun <T> transition() :  SpringSpec<T> = spring(
    dampingRatio = 1f,
    stiffness = 50f,
)

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun TransitionNavHost(
    state: NavStackState,
    modifier: Modifier = Modifier,
//    /** 当前 top 对应的共享容器 key，与 [SharedContainer] 的 key 一致；用于展开过程中按返回时打断共享容器动画。 */
//    sharedContainerKeyForEntry: (BackStackEntry) -> Any? = { null }
) {
    CompositionLocalProvider(
        LocalNavStackState provides state,
    ) {
//        val containerController = LocalSharedContainerController.current

        // 普通返回；展开过程中按返回时先打断共享容器动画再 pop
        BackHandler(enabled = state.stack.size > 1) {
            if (state.currentAction == NavActionState.PUSH_ING) {
//                containerController?.cancelAll()
            }
            state.navigate(NavCommand.Pop)
        }
        // 预测式返回 state.stack.size > 1
        PredictiveBackHandler(enabled = false) { progress ->
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


        val stack = state.stack
        val top = stack.lastOrNull()
        val under = stack.getOrNull(stack.lastIndex - 1)

        // 三态：NONE=稳定，PUSH_ING=新页进入中，POP_ING=顶层退出中
        // 「下层被盖住」= 有上层 且 (稳定态 或 PUSH 已到「待 commit」)。PUSH_ING 且未 pendingPush 时 isCovered=false
        val isCovered = stack.size > 1 && (
                state.currentAction == NavActionState.NONE ||
                        (state.currentAction == NavActionState.PUSH_ING && state.pendingPushEntryId)
                )

        val predictiveProgress = state.predictiveProgress
        val isInPredictive = predictiveProgress != 0f || top?.transitionState?.targetState == NavPhase.Predictive

        // When the under page is always composed, shared-container registrations on the under page
        // can re-trigger transitions during stable states. Disable them unless we are actually
        // transitioning or in predictive back.
        val enableUnderSharedContainer =
            isInPredictive || state.currentAction != NavActionState.NONE

        // 栈层数变化时重建 transition，避免 Second→Third 时沿用上一层的 duration=1，导致动画从 1→0 反了
        key(stack.size) {
            val coveredTransition = updateTransition(
                targetState = isCovered,
                label = "UnderCovered"
            )

            // Push：只有 coveredTransition 真正跑过（isRunning 曾为 true）后才允许 commit，避免 pendingPush 一设就立刻 commit
            var didRunCoveredTransitionForPush by remember { mutableStateOf(false) }
            LaunchedEffect(coveredTransition.isRunning, state.pendingPushEntryId) {
                if (state.pendingPushEntryId && coveredTransition.isRunning) {
                    didRunCoveredTransitionForPush = true
                }
                if (!state.pendingPushEntryId) {
                    didRunCoveredTransitionForPush = false
                }
            }

            // Pop：Exiting 结束后不立刻 commitPop，等下层 coveredTransition 结束后再移除
            // Push：pendingPush 且 coveredTransition 曾跑过且已结束，才 commitPush
            LaunchedEffect(
                state.pendingPopEntryId,
                state.pendingPushEntryId,
                coveredTransition.isRunning,
                didRunCoveredTransitionForPush
            ) {
                if (state.pendingPopEntryId != null && !coveredTransition.isRunning) {
                    val id = state.pendingPopEntryId!!
                    state.commitPop(id)
                }
                if (state.pendingPushEntryId && didRunCoveredTransitionForPush && !coveredTransition.isRunning) {
                    state.commitPush()
                }
            }

            // 正常情况返回时
            val backgroundDuration by coveredTransition.animateFloat(
                label = "backgroundDuration",
                transitionSpec = {
                    transition()
                }
            ) { covered ->
                if (covered) {
                    1f
                } else {
                    0f
                }
            }
            // 缩放单独拎出来，稍微慢一点
            val backgroundScaleDuration by coveredTransition.animateFloat(
                label = "backgroundDuration",
                transitionSpec = {
                    transition()
                }
            ) { covered ->
                if (covered) {
                    1f
                } else {
                    0f
                }
            }
            // 模糊单独拎出来，稍微慢一点
            val backgroundBlurDuration by coveredTransition.animateFloat(
                label = "backgroundDuration",
                transitionSpec = {
                    transition()
                }
            ) { covered ->
                if (covered) {
                    1f
                } else {
                    0f
                }
            }

            // 背景效果（进入时：Background -> Full，退出时：Full -> Background）
            val underEffect = UnderPageVisualEffect(
                scale = lerp(
                    UnderPageVisualEffect.Full.scale,
                    UnderPageVisualEffect.Background.scale,
                    backgroundScaleDuration
                ),
                blur = lerp(
                    UnderPageVisualEffect.Full.blur,
                    UnderPageVisualEffect.Background.blur,
                    backgroundBlurDuration
                ),
                mask = lerp(
                    UnderPageVisualEffect.Full.mask,
                    UnderPageVisualEffect.Background.mask,
                    backgroundDuration
                ),
                alpha = lerp(
                    UnderPageVisualEffect.Full.alpha,
                    UnderPageVisualEffect.Background.alpha,
                    backgroundDuration
                ),
                corner = lerp(
                    UnderPageVisualEffect.Full.corner,
                    UnderPageVisualEffect.Background.corner,
                    backgroundDuration
                )
            )

            // 主体效果（进入时：None -> Full，退出时：Full -> None）
            val topEffect = UnderPageVisualEffect(
                scale = lerp(
                    UnderPageVisualEffect.None.scale,
                    UnderPageVisualEffect.Full.scale,
                    backgroundScaleDuration
                ),
                blur = lerp(
                    UnderPageVisualEffect.None.blur,
                    UnderPageVisualEffect.Full.blur,
                    backgroundDuration
                ),
                mask = lerp(
                    UnderPageVisualEffect.None.mask,
                    UnderPageVisualEffect.Full.mask,
                    backgroundDuration
                ),
                alpha = lerp(
                    UnderPageVisualEffect.None.alpha,
                    UnderPageVisualEffect.Full.alpha,
                    backgroundDuration
                ),
                corner = lerp(
                    UnderPageVisualEffect.None.corner,
                    UnderPageVisualEffect.Full.corner,
                    backgroundDuration
                )
            )

            // 预测式返回时的背景和前景过渡（背景：Background -> PredictiveBackground，前景：Full -> PredictiveSelf）
            val predictiveUnderEffect = UnderPageVisualEffect(
                scale = lerp(
                    UnderPageVisualEffect.Background.scale,
                    UnderPageVisualEffect.PredictiveBackground.scale,
                    state.predictiveProgress
                ),
                blur = lerp(
                    UnderPageVisualEffect.Background.blur,
                    UnderPageVisualEffect.PredictiveBackground.blur,
                    state.predictiveProgress
                ),
                mask = lerp(
                    UnderPageVisualEffect.Background.mask,
                    UnderPageVisualEffect.PredictiveBackground.mask,
                    state.predictiveProgress
                ),
                alpha = lerp(
                    UnderPageVisualEffect.Background.alpha,
                    UnderPageVisualEffect.PredictiveBackground.alpha,
                    state.predictiveProgress
                ),
                corner = lerp(
                    UnderPageVisualEffect.Background.corner,
                    UnderPageVisualEffect.PredictiveBackground.corner,
                    state.predictiveProgress
                )
            )

            val predictiveTopEffect = UnderPageVisualEffect(
                scale = lerp(
                    UnderPageVisualEffect.Full.scale,
                    UnderPageVisualEffect.PredictiveSelf.scale,
                    state.predictiveProgress
                ),
                blur = lerp(
                    UnderPageVisualEffect.Full.blur,
                    UnderPageVisualEffect.PredictiveSelf.blur,
                    state.predictiveProgress
                ),
                mask = lerp(
                    UnderPageVisualEffect.Full.mask,
                    UnderPageVisualEffect.PredictiveSelf.mask,
                    state.predictiveProgress
                ),
                alpha = lerp(
                    UnderPageVisualEffect.Full.alpha,
                    UnderPageVisualEffect.PredictiveSelf.alpha,
                    state.predictiveProgress
                ),
                corner = lerp(
                    UnderPageVisualEffect.Full.corner,
                    UnderPageVisualEffect.PredictiveSelf.corner,
                    state.predictiveProgress
                )
            )

            // 下层：预测式用预测效果；否则一律用 underEffect（由 backgroundDuration 驱动：1=Background 被盖住，0=Full 露出）
            val underPageEffect = when {
                isInPredictive -> predictiveUnderEffect
                else -> underEffect
            }

            val topPageEffect = when {
                under == null -> underEffect   // 单页时用 Full，避免被缩小
                isInPredictive -> predictiveTopEffect
                else -> topEffect
            }


            // 背景/前景效果始终作用于上下层，与是否使用 SharedContainer 无关
            Box(modifier = Modifier.fillMaxSize().then(modifier)) {
                // 下层（背景）
                if (under != null) {
                    key(under.id) {
                        CompositionLocalProvider(
//                            LocalSharedContainerEnabled provides enableUnderSharedContainer
                        ) {
                            PageContainer(
                                entry = under,
                                transition = rememberTransition(
                                    under.transitionState,
                                    under.id
                                ),
                                isUnder = true,
                                effect = underPageEffect
                            )
                        }
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
                            effect = topPageEffect
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PageContainer(
    entry: BackStackEntry,
    transition: Transition<NavPhase>,
    isUnder: Boolean,
    effect: UnderPageVisualEffect
) {
    val state = LocalNavStackState.current

    // 状态闭环：动画到达目标且未在跑时回调（Push 只设 pending，由 LaunchedEffect 在 coveredTransition 结束后 commit）
    LaunchedEffect(transition.currentState, transition.targetState, transition.isRunning) {
        if (!transition.isRunning && transition.currentState == transition.targetState) {
            when (transition.currentState) {
                NavPhase.Active -> {
                    if (state.currentAction == NavActionState.PUSH_ING && entry.id == state.stack.lastOrNull()?.id) {
                        state.onEnteringTransitionComplete()  // 仅设 pendingPushEntryId，等 coveredTransition 结束后 commitPush()
                    }
                }
                NavPhase.Exiting -> state.onExitingTransitionComplete(entry.id)
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
//                it.scale(effect.scale)
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
