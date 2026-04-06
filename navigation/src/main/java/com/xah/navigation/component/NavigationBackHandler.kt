package com.xah.navigation.component

import android.os.Build
import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.runtime.Composable
import com.sharednav.common.util.LogUtil
import com.xah.container.model.SharedContainerState
import com.xah.navigation.util.LocalNavController
import kotlin.coroutines.cancellation.CancellationException

@Composable
fun NavigationBackHandler() {
    val navController = LocalNavController.current
    val canPop = navController.canPop()

    if(navController.enablePredictiveBack && Build.VERSION.SDK_INT >= 33) {
        PredictiveBackHandler(enabled = canPop) { backEvents ->
            var state : SharedContainerState? = null
            try {
                val transiting = navController.isTransitioning
                if(!transiting) {
                    state = navController.startPredictiveBackShared()
                    LogUtil.debug("startPredictiveBack")
                }
                backEvents.collect { backEvent ->
                    if(!transiting) {
                        val progress = backEvent.progress
                        navController.updatePredictiveBackShared(progress,state = state)
                        LogUtil.debug("updatePredictiveBack $progress")
                    }
                }
                if(transiting) {
                    navController.pop()
                } else {
                    navController.confirmPredictiveBackShared(state)
                    LogUtil.debug("confirmPredictiveBack")
                }
            } catch (e: CancellationException) {
                navController.cancelPredictiveBackShared(state)
                LogUtil.debug("cancelPredictiveBack")
            }
        }
    } else {
        BackHandler(enabled = canPop) {
            navController.pop()
        }
    }
}