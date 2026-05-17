package com.xah.floating.component

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import com.sharednav.common.util.LogUtil
import com.xah.container.model.SharedContainerState
import com.xah.container.util.LocalSharedRegistrySafely
import com.xah.floating.util.LocalFloatingController
import com.xah.floating.util.LocalFloatingControllerSafely
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun FloatingBackHandler() {
    val controller = LocalFloatingControllerSafely.current ?: return
    val registry = LocalSharedRegistrySafely.current
    val canPop = controller.isRunning
    if(registry?.enablePredictiveBack == true && Build.VERSION.SDK_INT >= 33) {
        PredictiveBackHandler(enabled = canPop) { backEvents ->
            var state : SharedContainerState? = null
            try {
                val transiting = registry.isRunning
                if(!transiting) {
                    state = controller.startPredictiveBackShared()
                    LogUtil.debug("startPredictiveBack")
                }
                backEvents.collect { backEvent ->
                    if(!transiting) {
                        val progress = backEvent.progress
                        controller.updatePredictiveBackShared(progress, Offset.Zero,state)
                        LogUtil.debug("updatePredictiveBack $progress")
                    }
                }
                if(transiting) {
                    controller.pop()
                } else {
                    controller.confirmPredictiveBackShared(state)
                    LogUtil.debug("confirmPredictiveBack")
                }
            } catch (e: CancellationException) {
                controller.cancelPredictiveBackShared(state)
                LogUtil.debug("cancelPredictiveBack")
                throw e
            }
        }
    } else {
        BackHandler(enabled = canPop) {
            controller.pop()
        }
    }
}
