package com.xah.navigation.model.dest

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xah.container.container.SharedContent
import com.xah.navigation.utils.LocalNavControllerSafely

abstract class Destination {
    /**
     * 也作为共享容器Key
     */
    abstract val key: String

    /**
     * 是否等动画完成后再加载，如果是则需要传一个PlaceHolder(Splash Screen)先显示，比如初始化相机时如果不延迟加载动效就会卡顿
     */
    open val PlaceHolder: (@Composable () -> Unit)? = null
    // ...可扩展

    @Composable
    abstract fun Content()

    @Composable
    fun Screen() {
        SharedContent(key) {
            Box(modifier = Modifier.fillMaxSize()) {
                if(PlaceHolder == null) {
                    Content()
                } else {
                    // 先加载占位符布局
                    val navController = LocalNavControllerSafely.current
                    if(navController == null) {
                        Content()
                    } else {
                        if(navController.isTransitioning) {
                            PlaceHolder!!()
                        } else {
                            Content()
                        }
                    }
                }
            }
        }
    }
}