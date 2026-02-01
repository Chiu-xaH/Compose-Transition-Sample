package com.xah.container

import android.view.Choreographer
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalContext
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.RecomposeScope
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import com.xah.container.SharedContainerTracker.State.Empty
import com.xah.container.SharedContainerTracker.State.EndContainerRegistered
import com.xah.container.SharedContainerTracker.State.InTransition
import com.xah.container.SharedContainerTracker.State.StartContainerPositioned
import com.xah.container.SharedContainerTracker.State.StartContainerRegistered
import com.xah.container.SharedContainerTransition.InProgress
import com.xah.container.SharedContainerTransition.WaitingForEndContainerPosition
import kotlinx.coroutines.delay

@Composable
internal fun BaseSharedContainer(
    containerInfo: SharedContainerInfo,
    isFullscreen: Boolean,
    placeholder: @Composable () -> Unit,
    overlay: @Composable (SharedContainerTransitionState) -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    val (savedShouldHide, setShouldHide) = remember { mutableStateOf(false) }
    val rootState = LocalSharedContainerRootState.current
    val shouldHide = rootState.onContainerRegistered(containerInfo)
    setShouldHide(shouldHide)

    val compositionLocalContext = currentCompositionLocalContext
    if (isFullscreen) {
        rootState.onContainerPositioned(
            containerInfo,
            compositionLocalContext,
            placeholder,
            overlay,
            null,
            setShouldHide
        )
        Spacer(modifier = Modifier.fillMaxSize())
    } else {
        val contentModifier = Modifier
            .onGloballyPositioned { coordinates ->
                rootState.onContainerPositioned(
                    containerInfo,
                    compositionLocalContext,
                    placeholder,
                    overlay,
                    coordinates,
                    setShouldHide
                )
            }
            .run { if (shouldHide || savedShouldHide) alpha(0f) else this }

        content(contentModifier)
    }

    DisposableEffect(containerInfo) {
        onDispose { rootState.onContainerDisposed(containerInfo) }
    }
}

@Composable
fun SharedContainerRoot(
    content: @Composable SharedContainerRootScope.() -> Unit
) {
    val rootState = remember { SharedContainerRootState() }
    val controller = remember(rootState) { SharedContainerControllerImpl(rootState) }

    Box(
        modifier = Modifier.onGloballyPositioned { layoutCoordinates ->
            rootState.rootCoordinates = layoutCoordinates
            rootState.rootBounds = Rect(Offset.Zero, layoutCoordinates.size.toSize())
        }
    ) {
        CompositionLocalProvider(
            LocalSharedContainerRootState provides rootState,
            LocalSharedContainerRootScope provides rootState.scope,
            LocalSharedContainerController provides controller
        ) {
            rootState.scope.content()
            UnboundedBox { SharedContainerTransitionsOverlay(rootState) }
        }
    }

    DisposableEffect(Unit) {
        onDispose { rootState.onDispose() }
    }
}

interface SharedContainerRootScope {
    val isRunningTransition: Boolean
    fun prepareTransition(vararg keys: Any)
}

val LocalSharedContainerRootScope = staticCompositionLocalOf<SharedContainerRootScope?> { null }

@Composable
private fun UnboundedBox(content: @Composable () -> Unit) {
    Layout(content) { measurables, constraints ->
        val infiniteConstraints = Constraints()
        val placeables = measurables.fastMap {
            val isFullscreen = it.layoutId === FullscreenLayoutId
            it.measure(if (isFullscreen) constraints else infiniteConstraints)
        }
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.fastForEach { it.place(0, 0) }
        }
    }
}

@Composable
private fun SharedContainerTransitionsOverlay(rootState: SharedContainerRootState) {
    rootState.recomposeScope = currentRecomposeScope
    rootState.trackers.forEach { (key, tracker) ->
        key(key) {
            val transition = tracker.transition
            val start = (tracker.state as? StartContainerPositioned)?.startContainer
            // Only draw overlay when actually in a transition; otherwise pages without SharedContainer
            // would be covered and lose background/foreground effects.
            val drawOverlay = transition != null || (start != null && start.bounds == null)
            if (drawOverlay) {
                val startContainer = start ?: transition!!.startContainer
                val startScreenKey = startContainer.info.screenKey
                val endContainer = (transition as? InProgress)?.endContainer
                val spec = startContainer.info.spec
                val animated = remember(startScreenKey) { Animatable(0f) }
                val fraction = animated.value
                startContainer.info.onFractionChanged?.invoke(fraction)
                endContainer?.info?.onFractionChanged?.invoke(1 - fraction)

                val direction = if (endContainer == null) null else remember(startScreenKey) {
                    val preferred = spec.direction
                    if (preferred != TransitionDirection.Auto) preferred else
                        calculateDirection(
                            startContainer.bounds ?: rootState.rootBounds!!,
                            endContainer.bounds ?: rootState.rootBounds!!
                        )
                }

                startContainer.Placeholder(
                    rootState = rootState,
                    fraction = fraction,
                    end = endContainer,
                    direction = direction,
                    spec = spec,
                    pathMotion = tracker.pathMotion
                )

                val interactive = tracker.interactiveProgress
                LaunchedEffect(interactive, animated) {
                    if (interactive != null) {
                        animated.snapTo(interactive)
                    }
                }

                val settleTarget = tracker.settleTarget
                val settleSpec = tracker.settleSpec
                if (settleTarget != null && settleSpec != null) {
                    LaunchedEffect(settleTarget, settleSpec, transition, animated) {
                        animated.animateTo(
                            targetValue = settleTarget,
                            animationSpec = settleSpec.animationSpec
//                                tween(
//                                durationMillis = settleSpec.durationMillis,
//                                delayMillis = settleSpec.delayMillis,
//                                easing = settleSpec.easing
//                            )
                        )
                        tracker.onSettleFinished(reached = settleTarget, transition = transition)
                    }
                } else if (transition is InProgress && interactive == null) {
                    LaunchedEffect(transition, animated) {
                        animated.animateTo(
                            targetValue = 1f,
                            animationSpec = spec.animationSpec
//                                tween(
//                                durationMillis = spec.durationMillis,
//                                delayMillis = spec.delayMillis,
//                                easing = spec.easing
//                            )
                        )
                        delay(spec.waitForFrames)
//                        repeat(spec.waitForFrames) { withFrameNanos {} }
                        transition.onTransitionFinished()
                    }
                }
            }
        }
    }
}

@Composable
private fun PositionedSharedContainer.Placeholder(
    rootState: SharedContainerRootState,
    fraction: Float,
    end: PositionedSharedContainer? = null,
    direction: TransitionDirection? = null,
    spec: SharedContainerTransitionSpec? = null,
    pathMotion: PathMotion? = null
) {
    overlay(
        SharedContainerTransitionState(
            fraction = fraction,
            startInfo = info,
            startBounds = if (end == null) bounds else bounds ?: rootState.rootBounds,
            startCompositionLocalContext = compositionLocalContext,
            startPlaceholder = placeholder,
            endInfo = end?.info,
            endBounds = end?.run { bounds ?: rootState.rootBounds },
            endCompositionLocalContext = end?.compositionLocalContext,
            endPlaceholder = end?.placeholder,
            direction = direction,
            spec = spec,
            pathMotion = pathMotion
        )
    )
}

private val LocalSharedContainerRootState = staticCompositionLocalOf<SharedContainerRootState> {
    error("SharedContainerRoot not found. SharedContainer must be hosted in SharedContainerRoot.")
}

private class SharedContainerRootState {
    private val choreographer = ChoreographerWrapper()
    val scope: SharedContainerRootScope = Scope()
    var trackers by mutableStateOf(mapOf<Any, SharedContainerTracker>())
    var recomposeScope: RecomposeScope? = null
    var rootCoordinates: LayoutCoordinates? = null
    var rootBounds: Rect? = null

    fun onContainerRegistered(containerInfo: SharedContainerInfo): Boolean {
        choreographer.removeCallback(containerInfo)
        return getTracker(containerInfo).onContainerRegistered(containerInfo)
    }

    fun onContainerPositioned(
        containerInfo: SharedContainerInfo,
        compositionLocalContext: CompositionLocalContext,
        placeholder: @Composable () -> Unit,
        overlay: @Composable (SharedContainerTransitionState) -> Unit,
        coordinates: LayoutCoordinates?,
        setShouldHide: (Boolean) -> Unit
    ) {
        val container = PositionedSharedContainer(
            info = containerInfo,
            compositionLocalContext = compositionLocalContext,
            placeholder = placeholder,
            overlay = overlay,
            bounds = coordinates?.calculateBoundsInRoot()
        )
        getTracker(containerInfo).onContainerPositioned(container, setShouldHide)
    }

    fun onContainerDisposed(containerInfo: SharedContainerInfo) {
        choreographer.postCallback(containerInfo) {
            val tracker = getTracker(containerInfo)
            tracker.onContainerUnregistered(containerInfo)
            if (tracker.isEmpty) trackers = trackers - containerInfo.key
        }
    }

    fun onDispose() {
        choreographer.clear()
    }

    private fun getTracker(containerInfo: SharedContainerInfo): SharedContainerTracker {
        return trackers[containerInfo.key] ?: SharedContainerTracker { transition ->
            recomposeScope?.invalidate()
            (scope as Scope).isRunningTransition = transition != null ||
                trackers.values.any { it.transition != null }
        }.also { trackers = trackers + (containerInfo.key to it) }
    }

    private fun LayoutCoordinates.calculateBoundsInRoot(): Rect =
        Rect(
            rootCoordinates?.localPositionOf(this, Offset.Zero) ?: positionInRoot(),
            size.toSize()
        )

    private inner class Scope : SharedContainerRootScope {
        override var isRunningTransition: Boolean by mutableStateOf(false)

        override fun prepareTransition(vararg keys: Any) {
            keys.forEach { trackers[it]?.prepareTransition() }
        }
    }
}

private class SharedContainerTracker(
    private val onTransitionChanged: (SharedContainerTransition?) -> Unit
) {
    var state: State = Empty
    var pathMotion: PathMotion? = null
    var endSetShouldHide: ((Boolean) -> Unit)? = null

    // Interactive driving.
    var interactiveProgress: Float? by mutableStateOf(null)
        private set

    var settleTarget: Float? by mutableStateOf(null)
        private set

    var settleSpec: SharedContainerTransitionSpec? by mutableStateOf(null)
        private set

    // Snapshot state to trigger recomposition when transition starts.
    private var _transition: SharedContainerTransition? by mutableStateOf(null)
    var transition: SharedContainerTransition?
        get() = _transition
        set(value) {
            if (_transition != value) {
                _transition = value
                if (value == null) {
                    pathMotion = null
                    interactiveProgress = null
                    settleTarget = null
                    settleSpec = null
                    endSetShouldHide = null
                }
                onTransitionChanged(value)
            }
        }

    /** After a transition finishes, the "other" screen (previous start, now under) may still
     * recompose and re-register. Ignore that re-registration to avoid a spurious second transition. */
    var stableOtherScreenKey: Any? by mutableStateOf(null)
        private set

    val isEmpty: Boolean get() = state is Empty

    private fun StartContainerPositioned.prepareTransition() {
        if (transition !is WaitingForEndContainerPosition) {
            transition = WaitingForEndContainerPosition(startContainer)
        }
    }

    fun prepareTransition() {
        (state as? StartContainerPositioned)?.prepareTransition()
    }

    fun onContainerRegistered(containerInfo: SharedContainerInfo): Boolean {
        var shouldHide = false

        val transition = transition
        if (transition is InProgress &&
            containerInfo != transition.startContainer.info &&
            containerInfo != transition.endContainer.info
        ) {
            state = StartContainerPositioned(startContainer = transition.endContainer)
            this.transition = null
        }

        when (val cur = state) {
            is StartContainerPositioned -> {
                if (!cur.isRegistered(containerInfo)) {
                    if (containerInfo.screenKey == stableOtherScreenKey) {
                        return shouldHide || transition != null
                    }
                    stableOtherScreenKey = null
                    shouldHide = true
                    state = EndContainerRegistered(
                        startContainer = cur.startContainer,
                        endContainerInfo = containerInfo
                    )
                    cur.prepareTransition()
                }
            }
            is StartContainerRegistered -> {
                if (containerInfo != cur.startContainerInfo) {
                    state = StartContainerRegistered(startContainerInfo = containerInfo)
                }
            }
            is Empty -> {
                state = StartContainerRegistered(startContainerInfo = containerInfo)
            }
            else -> Unit
        }

        return shouldHide || transition != null
    }

    fun onContainerPositioned(container: PositionedSharedContainer, setShouldHide: (Boolean) -> Unit) {
        val curState = state
        if (curState is StartContainerPositioned && container.info == curState.startContainerInfo) {
            curState.startContainer = container
            return
        }

        when (curState) {
            is EndContainerRegistered -> {
                if (container.info == curState.endContainerInfo) {
                    state = InTransition
                    val spec = container.info.spec
                    pathMotion = spec.pathMotionFactory()
                    endSetShouldHide = setShouldHide
                    val oldStartScreenKey = curState.startContainer.info.screenKey
                    transition = InProgress(
                        startContainer = curState.startContainer,
                        endContainer = container,
                        onTransitionFinished = {
                            stableOtherScreenKey = oldStartScreenKey
                            state = StartContainerPositioned(startContainer = container)
                            transition = null
                            setShouldHide(false)
                        }
                    )
                }
            }
            is StartContainerRegistered -> {
                if (container.info == curState.startContainerInfo) {
                    state = StartContainerPositioned(startContainer = container)
                }
            }
            else -> Unit
        }
    }

    fun beginInteractive() {
        settleTarget = null
        settleSpec = null
        if (interactiveProgress == null) interactiveProgress = 0f
    }

    fun updateInteractiveProgress(progress: Float) {
        interactiveProgress = progress.coerceIn(0f, 1f)
    }

    fun commitInteractive() {
        val spec = when (val t = transition) {
            is InProgress -> t.startContainer.info.spec
            else -> DefaultSharedContainerTransitionSpec
        }
        interactiveProgress = null
        settleTarget = 1f
        settleSpec = spec
    }

    fun cancelInteractive() {
        val spec = when (val t = transition) {
            is InProgress -> t.startContainer.info.spec
            else -> DefaultSharedContainerTransitionSpec
        }
        interactiveProgress = null
        settleTarget = 0f
        settleSpec = spec
    }

    fun onSettleFinished(reached: Float, transition: SharedContainerTransition?) {
        settleTarget = null
        settleSpec = null

        when {
            reached >= 1f && transition is InProgress -> {
                stableOtherScreenKey = transition.startContainer.info.screenKey
                transition.onTransitionFinished()
            }
            reached <= 0f -> {
                // Cancel: drop the transition and unhide end if it was hidden.
                // Ignore end's re-registration so we don't trigger a spurious second expand/collapse.
                if (transition is InProgress) stableOtherScreenKey = transition.endContainer.info.screenKey
                endSetShouldHide?.invoke(false)
                val start = (state as? StartContainerPositioned)?.startContainer
                    ?: (transition as? InProgress)?.startContainer
                this.transition = null
                state = if (start != null) StartContainerPositioned(startContainer = start) else Empty
            }
        }
    }

    fun onContainerUnregistered(containerInfo: SharedContainerInfo) {
        if (containerInfo.screenKey == stableOtherScreenKey) stableOtherScreenKey = null
        when (val cur = state) {
            is EndContainerRegistered -> {
                if (containerInfo == cur.endContainerInfo) {
                    state = StartContainerPositioned(startContainer = cur.startContainer)
                    transition = null
                } else if (containerInfo == cur.startContainer.info) {
                    state = StartContainerRegistered(startContainerInfo = cur.endContainerInfo)
                    transition = null
                }
            }
            is StartContainerRegistered -> {
                if (containerInfo == cur.startContainerInfo) {
                    state = Empty
                    transition = null
                }
            }
            else -> Unit
        }
    }

    sealed class State {
        data object Empty : State()

        open class StartContainerRegistered(val startContainerInfo: SharedContainerInfo) : State() {
            open fun isRegistered(containerInfo: SharedContainerInfo): Boolean =
                containerInfo == startContainerInfo
        }

        open class StartContainerPositioned(var startContainer: PositionedSharedContainer) :
            StartContainerRegistered(startContainer.info)

        class EndContainerRegistered(
            startContainer: PositionedSharedContainer,
            val endContainerInfo: SharedContainerInfo
        ) : StartContainerPositioned(startContainer) {
            override fun isRegistered(containerInfo: SharedContainerInfo): Boolean =
                super.isRegistered(containerInfo) || containerInfo == endContainerInfo
        }

        data object InTransition : State()
    }
}

enum class TransitionDirection {
    Auto, Enter, Return
}

enum class FadeMode {
    In, Out, Cross, Through
}

const val FadeThroughProgressThreshold: Float = 0.35f

internal class SharedContainerTransitionState(
    val fraction: Float,
    val startInfo: SharedContainerInfo,
    val startBounds: Rect?,
    val startCompositionLocalContext: CompositionLocalContext,
    val startPlaceholder: @Composable () -> Unit,
    val endInfo: SharedContainerInfo?,
    val endBounds: Rect?,
    val endCompositionLocalContext: CompositionLocalContext?,
    val endPlaceholder: (@Composable () -> Unit)?,
    val direction: TransitionDirection?,
    val spec: SharedContainerTransitionSpec?,
    val pathMotion: PathMotion?
)

internal open class SharedContainerInfo(
    val key: Any,
    val screenKey: Any,
    val color: Color,
    val cornerRadius: Dp,
    val spec: SharedContainerTransitionSpec,
    val onFractionChanged: ((Float) -> Unit)?
) {
    final override fun equals(other: Any?): Boolean =
        other is SharedContainerInfo && other.key == key && other.screenKey == screenKey

    final override fun hashCode(): Int = 31 * key.hashCode() + screenKey.hashCode()
}

private class PositionedSharedContainer(
    val info: SharedContainerInfo,
    val compositionLocalContext: CompositionLocalContext,
    val placeholder: @Composable () -> Unit,
    val overlay: @Composable (SharedContainerTransitionState) -> Unit,
    val bounds: Rect?
)

private sealed class SharedContainerTransition(val startContainer: PositionedSharedContainer) {
    class WaitingForEndContainerPosition(startContainer: PositionedSharedContainer) :
        SharedContainerTransition(startContainer)

    class InProgress(
        startContainer: PositionedSharedContainer,
        val endContainer: PositionedSharedContainer,
        val onTransitionFinished: () -> Unit
    ) : SharedContainerTransition(startContainer)
}

private class ChoreographerWrapper {
    private val callbacks = mutableMapOf<SharedContainerInfo, Choreographer.FrameCallback>()
    private val choreographer = Choreographer.getInstance()

    fun postCallback(containerInfo: SharedContainerInfo, callback: () -> Unit) {
        if (callbacks.containsKey(containerInfo)) return
        val frameCallback = Choreographer.FrameCallback {
            callbacks.remove(containerInfo)
            callback()
        }
        callbacks[containerInfo] = frameCallback
        choreographer.postFrameCallback(frameCallback)
    }

    fun removeCallback(containerInfo: SharedContainerInfo) {
        callbacks.remove(containerInfo)?.also(choreographer::removeFrameCallback)
    }

    fun clear() {
        callbacks.values.forEach(choreographer::removeFrameCallback)
        callbacks.clear()
    }
}

internal val Fullscreen = Modifier.fillMaxSize()
internal val FullscreenLayoutId = Any()

private class SharedContainerControllerImpl(
    private val rootState: SharedContainerRootState
) : SharedContainerController {
    override fun prepare(key: Any) {
        rootState.trackers[key]?.prepareTransition()
        rootState.recomposeScope?.invalidate()
    }

    override fun begin(key: Any) {
        rootState.trackers[key]?.beginInteractive()
        rootState.recomposeScope?.invalidate()
    }

    override fun updateProgress(key: Any, progress: Float) {
        rootState.trackers[key]?.updateInteractiveProgress(progress)
        rootState.recomposeScope?.invalidate()
    }

    override fun commit(key: Any) {
        rootState.trackers[key]?.commitInteractive()
        rootState.recomposeScope?.invalidate()
    }

    override fun cancel(key: Any) {
        rootState.trackers[key]?.cancelInteractive()
        rootState.recomposeScope?.invalidate()
    }
}

@Composable
internal fun SharedContainerOverlayPlaceholder(state: SharedContainerTransitionState) {
    with(LocalDensity.current) {
        val startBounds = state.startBounds
        val endBounds = state.endBounds
        val startInfo = state.startInfo
        val endInfo = state.endInfo

        val fraction = state.fraction
        val fadeFraction = state.spec?.fadeProgressThresholds?.applyTo(fraction) ?: fraction

        val direction = state.direction
        val fadeMode = state.spec?.fadeMode

        // Container visual (color/corner) interpolation.
        val endColor = endInfo?.color ?: startInfo.color
        val endCorner = endInfo?.cornerRadius ?: startInfo.cornerRadius
        val containerColor = androidx.compose.ui.graphics.lerp(startInfo.color, endColor, fraction)
        val containerCorner = lerp(startInfo.cornerRadius, endCorner, fraction)

        if (startBounds == null) {
            // Fullscreen fallback (e.g. start isFullscreen without an end yet).
            Box(
                modifier = Fullscreen
                    .layoutId(FullscreenLayoutId)
                    .clip(RoundedCornerShape(containerCorner))
                    .background(containerColor)
            ) {
                SharedContainerOverlayContentLayers(
                    state = state,
                    fadeFraction = fadeFraction,
                    direction = direction,
                    fadeMode = fadeMode
                )
            }
            return
        }

        val topCenter = calculateTopCenter(startBounds, endBounds, fraction, state.pathMotion)
        val size = if (endBounds == null) {
            startBounds.size
        } else {
            lerpSize(startBounds.size, endBounds.size, fraction)
        }
        val offset = Offset(topCenter.x - size.width / 2f, topCenter.y).round()

        Box(
            modifier = Modifier
                .size(size.width.toDp(), size.height.toDp())
                .offset { offset }
                .clip(RoundedCornerShape(containerCorner))
                .background(containerColor)
        ) {
            SharedContainerOverlayContentLayers(
                state = state,
                fadeFraction = fadeFraction,
                direction = direction,
                fadeMode = fadeMode
            )
        }
    }
}

@Composable
private fun SharedContainerOverlayContentLayers(
    state: SharedContainerTransitionState,
    fadeFraction: Float,
    direction: TransitionDirection?,
    fadeMode: FadeMode?
) {
    // Route A: draw both start/end placeholders in overlay, and crossfade them.
    fun contentAlpha(isStart: Boolean): Float =
        calculateAlpha(direction = direction, fadeMode = fadeMode, fraction = fadeFraction, isStart = isStart)

    // End layer first when FadeMode.Out (end should be behind).
    val endFirst = fadeMode == FadeMode.Out

    @Composable
    fun StartLayer() {
        val alpha = contentAlpha(true)
        if (alpha <= 0f) return
        CompositionLocalProvider(state.startCompositionLocalContext) {
            ElementContainer(modifier = Modifier.fillMaxSize().alpha(alpha)) {
                state.startPlaceholder()
            }
        }
    }

    @Composable
    fun EndLayer() {
        val endPlaceholder = state.endPlaceholder ?: return
        val endContext = state.endCompositionLocalContext ?: return
        val alpha = contentAlpha(false)
        if (alpha <= 0f) return
        CompositionLocalProvider(endContext) {
            ElementContainer(modifier = Modifier.fillMaxSize().alpha(alpha)) {
                endPlaceholder()
            }
        }
    }

    if (endFirst) {
        EndLayer()
        StartLayer()
    } else {
        StartLayer()
        EndLayer()
    }
}

