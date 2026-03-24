package com.xah.floating.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xah.floating.anim.DefaultEffects
import com.xah.floating.anim.PageEffects
import com.xah.floating.anim.backgroundEffect
import com.xah.floating.controller.FloatingController
import com.xah.floating.controller.FloatingViewModel
import com.xah.floating.util.LocalFloatingController
import com.xah.floating.util.LocalFloatingControllerSafely

@Composable
fun rememberFloatingController(
    effect: PageEffects = DefaultEffects,
): FloatingController {
    val scope = rememberCoroutineScope()
    val vm: FloatingViewModel = viewModel(factory = FloatingViewModel.Factory())
    return remember(vm) {
        FloatingController(scope, vm.stack, effect)
    }
}

@Composable
fun FloatingRoot(
    controller: FloatingController = rememberFloatingController(),
    backHandler : @Composable () -> Unit = { FloatingBackHandler() },
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalFloatingControllerSafely provides controller,
        LocalFloatingController provides controller,
    ) {
        val isRunning = controller.isRunning
        val progress by animateFloatAsState(
            targetValue = if(isRunning) 0f else 1f,
            animationSpec = controller.effect.backgroundEffect.animationSpec
        )
        val effect = controller.effect.backgroundEffect.pageEffect.lerp(progress)

        backHandler()

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.backgroundEffect(
                    controller.enableShader,
                    controller.enableBlur,
                    effect
                )
            ) {
                content()
            }
            FloatingOverlay()
        }
    }
}
