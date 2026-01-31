package com.xah.navigation.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.xah.navigation.model.BackStackEntry
import com.xah.navigation.model.Destination
import com.xah.navigation.model.NavActionState
import com.xah.navigation.model.NavCommand
import com.xah.navigation.model.NavPhase
import java.util.UUID
import kotlin.collections.plusAssign

class NavStackState(
    startDestination : Destination
) {
    private val _stack = mutableStateListOf<BackStackEntry>()
    val stack: List<BackStackEntry> get() = _stack

    var currentAction by mutableStateOf<NavActionState>(NavActionState.NONE)
        private set

    /** 上层 Entering 动画完成时调用，仅设 pendingPush；等 coveredTransition 结束后由 commitPush() 置 NONE */
    fun onEnteringTransitionComplete() {
        pendingPushEntryId = true
    }

    /** 上层 Exiting 动画已结束、等待下层 coveredTransition 结束后再真正移除的 entry id */
    var pendingPopEntryId by mutableStateOf<String?>(null)
        private set

    /** Push：Entering 动画已结束，等待下层 coveredTransition 结束后由 commitPush() 置 NONE */
    var pendingPushEntryId by mutableStateOf(false)
        private set

    /** 上层 Exiting 动画完成时调用，不立刻移除，等下层动画结束后由 commitPop 移除 */
    fun onExitingTransitionComplete(entryId: String) {
        pendingPopEntryId = entryId
    }

    /** Push 两阶段之 commit：coveredTransition 结束后由 UI 调用，置 NONE */
    fun commitPush() {
        currentAction = NavActionState.NONE
        pendingPushEntryId = false
    }

    // 预测进度（0f ~ 1f）
    var predictiveProgress by mutableFloatStateOf(0f)
        private set

    fun beginPredictivePop() {
        if (_stack.size <= 1) return
        predictiveProgress = 0f
        _stack.last().transitionState.targetState = NavPhase.Predictive
    }

    fun updatePredictiveProgress(progress: Float) {
        predictiveProgress = progress
    }

    fun cancelPredictivePop() {
        predictiveProgress = 0f
        val top = _stack.lastOrNull() ?: return
        top.transitionState.targetState = NavPhase.Active
    }

    /** 预测式返回确认：与 requestPop() 一致，进入 Exiting 并标记 POP_ING */
    fun commitPredictivePop() {
        predictiveProgress = 0f
        val top = _stack.lastOrNull() ?: return
        currentAction = NavActionState.POP_ING
        top.transitionState.targetState = NavPhase.Exiting
    }

    fun navigate(command: NavCommand) {
        when (command) {
            is NavCommand.Push -> requestPush(command.destination)
            is NavCommand.Pop -> requestPop()
        }
    }

    /** Push 两阶段之 request：开始 push，置 PUSH_ING；Entering 完成后 UI 调 onEnteringTransitionComplete，coveredTransition 结束后 UI 调 commitPush */
    fun requestPush(destination: Destination) {
        currentAction = NavActionState.PUSH_ING
        val entry = BackStackEntry(
            id = UUID.randomUUID().toString(),
            destination = destination,
        )
        entry.transitionState.targetState = NavPhase.Active
        _stack += entry
    }

    fun requestPop() {
        if (_stack.size <= 1) return
        currentAction = NavActionState.POP_ING
        _stack.last().transitionState.targetState = NavPhase.Exiting
    }

    fun commitPop(entryId: String) {
        _stack.removeAll { it.id == entryId }
        currentAction = NavActionState.NONE
        pendingPopEntryId = null
    }



    init {
        val rootEntry = BackStackEntry(
            id = UUID.randomUUID().toString(),
            destination = startDestination
        )
        rootEntry.transitionState.targetState = NavPhase.Active
        _stack += rootEntry
    }
}