package com.xah.navigation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.xah.container.container.SharedContent
import com.xah.container.overlay.SharedContainerRoot
import com.xah.container.utils.LocalSharedRegistry
import com.xah.navigation.anim.EffectLevel
import com.xah.navigation.anim.PageEffects
import com.xah.navigation.anim.backgroundEffect
import com.xah.navigation.anim.foregroundEffect
import com.xah.navigation.anim.rememberDefaultPageEffects
import com.xah.navigation.controller.NavigationController
import com.xah.navigation.controller.NavigationViewModel
import com.xah.navigation.model.Dependencies
import com.xah.navigation.model.action.ActionType
import com.xah.navigation.model.dest.Destination
import com.xah.navigation.utils.LocalNavController
import com.xah.navigation.utils.LocalNavControllerSafely
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.navigation.utils.touchEvent

@Composable
fun rememberNavController(
    startDestination : Destination,
    keepPreviousPage : Boolean = false
): NavigationController {
    val scope = rememberCoroutineScope()
    val navViewModel: NavigationViewModel = viewModel(factory = NavigationViewModel.Factory())
    val navController = remember(navViewModel) {
        NavigationController(scope, startDestination, keepPreviousPage, navViewModel.stack,null)
    }
    return navController
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SharedNavHost(
    navController: NavigationController,
    modifier: Modifier = Modifier,
    effect: PageEffects = rememberDefaultPageEffects(),
    dependencies: Dependencies = Dependencies(),
    backHandler: (@Composable () -> Unit) = { DefaultBackHandler() },
) {
    SharedContainerRoot(navController.keepPrevious) {
        NavHost(
            navController,
            modifier,
            effect,
            dependencies,
            backHandler
        )
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun SharedNavHost(
    startDestination: Destination,
    modifier: Modifier = Modifier,
    keepPreviousPage : Boolean = false,
    effect: PageEffects = rememberDefaultPageEffects(),
    dependencies: Dependencies = Dependencies(),
    backHandler: (@Composable () -> Unit) = { DefaultBackHandler() },
) {
    val navController = rememberNavController(startDestination,keepPreviousPage)
    SharedNavHost(
        navController,
        modifier,
        effect,
        dependencies,
        backHandler
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavHost(
    startDestination: Destination,
    modifier: Modifier = Modifier,
    keepPreviousPage : Boolean = false,
    effect: PageEffects = rememberDefaultPageEffects(),
    dependencies: Dependencies = Dependencies(),
    customBackHandler: (@Composable () -> Unit) = { DefaultBackHandler() },
) {
    val navController = rememberNavController(startDestination,keepPreviousPage)

    NavHost(
        navController,
        modifier,
        effect,
        dependencies,
        customBackHandler
    )
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun NavHost(
    navController: NavigationController,
    modifier: Modifier = Modifier,
    effect: PageEffects = rememberDefaultPageEffects(),
    dependencies: Dependencies = Dependencies(),
    backHandler: (@Composable () -> Unit) = { DefaultBackHandler() },
) {
    val registry = LocalSharedRegistry.current
    val saveableStateHolder = rememberSaveableStateHolder()

    LaunchedEffect(registry) {
        navController.sharedRegistry = registry
    }

    LaunchedEffect(navController.keepPrevious) {
        registry.needWaitMultiFrame = !navController.keepPrevious
    }

    CompositionLocalProvider(
        LocalNavControllerSafely provides navController,
        LocalNavController provides navController,
        LocalNavDependencies provides dependencies
    ) {
        backHandler()

        val transition = navController.navTransition
        val progress = navController.transitionProgress

        // 当 transition 变化时启动动画
        LaunchedEffect(transition,registry.isRunning,registry.isWaitingFrame) {
            navController.animate()
        }

        val visibleEntries = remember(transition) {
            when (transition?.type) {
                ActionType.POP -> listOf(transition.to, transition.from)
                ActionType.PUSH -> listOf(transition.from, transition.to)
                else -> listOf(navController.stack.last())
            }
        }

        val level = navController.transitionLevel
        val enableBlur = navController.enableBlur
        val enableShader = navController.enableShader

        Box(modifier = modifier.fillMaxSize()) {
            visibleEntries.forEach { entry ->
                key(entry.id) {
                    saveableStateHolder.SaveableStateProvider(entry.id) {
                        val isFrom = transition?.from == entry
                        val isTo = transition?.to == entry

                        val animatedProgress = progress.value

                        val backgroundEffect = remember(animatedProgress,level) { effect.background(animatedProgress,level) }
                        val foregroundEffect = remember(animatedProgress,level) { effect.foreground(animatedProgress, level) }
                        val foregroundOrigin = remember(level) { effect.foregroundOrigin(level) }

                        Box(
                            Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    // 容器等帧测量时，将目标测量（isTo）内容隐藏，避免闪烁
                                    if(transition != null && isTo && registry.isWaitingFrame) {
                                        alpha = 0f
                                    }
                                }
                                .let {
                                    // 容器等帧测量时，禁用所有动效，测量容器的真实位置
                                    if (transition != null && !registry.isWaitingFrame) {
                                        when (transition.type) {
                                            ActionType.PUSH -> {
                                                if (isFrom) {
                                                    // 背景
                                                    return@let it.backgroundEffect(
                                                        enableShader,
                                                        enableBlur,
                                                        registry.isRunning,
                                                        backgroundEffect
                                                    )
                                                }
                                                if (isTo) {
                                                    // 目标屏幕
                                                    if(!registry.isRunning) {
                                                        return@let it.foregroundEffect(
                                                            enableBlur,
                                                            foregroundEffect,
                                                            foregroundOrigin
                                                        )
                                                    }
                                                }
                                            }
                                            ActionType.POP -> {
                                                if (isTo) {
                                                    // 背景
                                                    return@let it.backgroundEffect(
                                                        enableShader,
                                                        enableBlur,
                                                        registry.isRunning,
                                                        backgroundEffect
                                                    )
                                                }
                                                if (isFrom) {
                                                    // 退出屏幕
                                                    if(!registry.isRunning) {
                                                        return@let it.foregroundEffect(
                                                            enableBlur,
                                                            foregroundEffect,
                                                            foregroundOrigin
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    return@let it
                                }
                                .touchEvent(
                                    transition == null
                                    // 当返回时，禁用前景；当前进时，禁用背景；当非动画态，启用
                                    // TODO 暂时一刀切，未适配并行动画
                                )
                        ) {
                            SharedContent(entry.destination.key) {
                                val needDisplaySplashScreen = entry.destination.enforcePlaceHolder || (navController.enableSplashScreen && navController.transitionLevel != EffectLevel.NONE)
                                // NONE等级动效不需要遮罩
                                val enableSplashScreen = needDisplaySplashScreen && entry.destination.PlaceHolder != null
                                // 动画过程中且为前景
                                val inTransiting = (transition?.type == ActionType.POP && isFrom && navController.isTransitioning) || (transition?.type == ActionType.PUSH && isTo)
                                if(enableSplashScreen && inTransiting) {
                                    entry.destination.PlaceHolder!!.invoke()
                                } else {
                                    entry.destination.Content()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}