package com.xah.transition.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.xah.transition.ui.model.BackStackEntry
import com.xah.transition.ui.model.Destination
import com.xah.transition.ui.model.NavCommand
import com.xah.transition.ui.model.NavPhase
import java.util.UUID

class NavStackState(
    startDestination : Destination
) {
    private val _stack = mutableStateListOf<BackStackEntry>()
    val stack: List<BackStackEntry> get() = _stack

    var isPopping by mutableStateOf(false)
        private set

    // 预测进度（0f ~ 1f）
    var predictiveProgress by mutableFloatStateOf(0f)
        private set

//    fun beginPredictivePop() {
//        if (_stack.size <= 1) return
//        val top = _stack.last()
//        top.transitionState.targetState = NavPhase.Predictive
//    }
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

    fun commitPredictivePop() {
        predictiveProgress = 0f
        val top = _stack.lastOrNull() ?: return
        top.transitionState.targetState = NavPhase.Exiting
    }

    fun navigate(command: NavCommand) {
        when (command) {
            is NavCommand.Push -> pushDestination(command.destination)
            is NavCommand.Pop -> requestPop()
        }
    }

//    fun requestPop() {
//        if (_stack.size <= 1) return
//        _stack.last().transitionState.targetState = NavPhase.Exiting
//    }
    fun requestPop() {
        if (_stack.size <= 1) return
        isPopping = true
        _stack.last().transitionState.targetState = NavPhase.Exiting
    }


//    fun commitPop(entryId: String) {
//        _stack.removeAll { it.id == entryId }
//    }
    fun commitPop(entryId: String) {
        _stack.removeAll { it.id == entryId }
        isPopping = false
    }


    private fun pushDestination(dest: Destination) {
        val entry = BackStackEntry(
            id = UUID.randomUUID().toString(),
            destination = dest
        )
        entry.transitionState.targetState = NavPhase.Entering
        _stack += entry
    }

    init {
        val rootEntry = BackStackEntry(
            id = UUID.randomUUID().toString(),
            destination = startDestination
        )
        rootEntry.transitionState.targetState = NavPhase.Active
        _stack += rootEntry
    }
/*
    fun requestPop() {
        if (_stack.size <= 1) return

        val top = _stack.last()
        top.transitionState.targetState = NavPhase.Exiting
    }

    fun commitPop(entryId: String) {
        val index = _stack.indexOfFirst { it.id == entryId }
        if (index != -1) {
            _stack.removeAt(index)
        }
    }

    fun navigate(command: NavCommand) {
        when (command) {
            is NavCommand.Push -> pushDestination(command.destination)
            is NavCommand.Pop -> requestPop()
        }
    }


    private fun pushDestination(dest: Destination) {
        val entry = BackStackEntry(
            id = UUID.randomUUID().toString(),
            destination = dest
        )
        entry.transitionState.targetState = NavPhase.Entering
        _stack += entry
    }

 */
}
