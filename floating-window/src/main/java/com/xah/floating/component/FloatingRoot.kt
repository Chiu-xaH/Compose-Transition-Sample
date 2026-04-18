package com.xah.floating.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xah.container.controller.SharedRegistry
import com.xah.container.util.LocalSharedRegistrySafely
import com.xah.floating.anim.DefaultBackgroundEffect
import com.xah.floating.anim.DefaultEffects
import com.xah.floating.anim.backgroundEffect
import com.xah.floating.controller.FloatingController
import com.xah.floating.controller.FloatingViewModel
import com.xah.floating.model.anim.BackgroundEffect
import com.xah.floating.model.anim.PageEffects
import com.xah.floating.util.LocalFloatingController
import com.xah.floating.util.LocalFloatingControllerSafely

@Composable
fun rememberFloatingController(
    backgroundEffect: BackgroundEffect = DefaultBackgroundEffect,
): FloatingController {
    val scope = rememberCoroutineScope()
    val vm: FloatingViewModel = viewModel(factory = FloatingViewModel.Factory())
    return remember(vm) {
        FloatingController(scope, vm.stack,vm.inOverlay, backgroundEffect)
    }
}

@Composable
fun FloatingRoot(
    controller: FloatingController = rememberFloatingController(),
    registry: SharedRegistry? = null,
    backHandler : @Composable () -> Unit = { FloatingBackHandler() },
    content: @Composable () -> Unit,
) {
    LaunchedEffect(registry) {
        controller.sharedRegistry = registry
    }
    FloatingRoot(controller,backHandler,content)
}

@Composable
fun FloatingRoot(
    controller: FloatingController = rememberFloatingController(),
    backHandler : @Composable () -> Unit = { FloatingBackHandler() },
    content: @Composable () -> Unit,
) {
    val inOverlay = controller.inOverlay
    CompositionLocalProvider(
        LocalFloatingControllerSafely provides controller,
        LocalFloatingController provides controller,
    ) {
        val registry = LocalSharedRegistrySafely.current
        val animationSpec = if(registry?.isRunning == true) {
            if(inOverlay) {
                registry.getPopAnimation()
            } else {
                registry.getPushAnimation()
            }
        } else {
            controller.backgroundEffect.animationSpec
        }

        val progress by animateFloatAsState(
            targetValue = if(inOverlay) 0f else 1f,
            animationSpec = animationSpec
        )
        val effect = controller.backgroundEffect.pageEffect.lerp(progress)

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
