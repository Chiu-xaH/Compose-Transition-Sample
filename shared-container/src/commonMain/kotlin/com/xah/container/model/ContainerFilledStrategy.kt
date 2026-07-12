package com.xah.container.model

import com.sharednav.common.util.EnableHelper
import com.xah.container.controller.SharedRegistry

sealed interface ContainerFilledStrategy {
    /**
     * 指定颜色填充底部（类似老版本Flyme）
     */
    data class Color(val color : androidx.compose.ui.graphics.Color) : ContainerFilledStrategy

    /**
     * 竖屏取底部1像素做填充，横屏取右侧1像素做填充（类似iOS，目前主流方案）
     * @param spareStrategy Require SDK33+ 若低版本使用此效果则降级为spareStrategy，推荐使用Color方案作为备用
     */
    data class Pixel(val spareStrategy : ContainerFilledStrategy = Clip) : ContainerFilledStrategy

    /**
     * 裁切放大（类似OriginOS 1.0）
     */
    data object Clip : ContainerFilledStrategy

    /**
     * 拉伸填充（类似老版本EMUI）
     * 观感一般，优先考虑 Clip
     */
    data object Stretch : ContainerFilledStrategy

    /**
     * 元素共享专用，不受enforceContainerFilledStrategy限制，对Content做透明度渐变，并置于中央裁切
     */
    data object Element : ContainerFilledStrategy

    fun getFinalStrategy(sharedRegistry: SharedRegistry) : ContainerFilledStrategy {
        if(this is Element) {
            return this
        }
        return sharedRegistry.enforceContainerFilledStrategy
            ?:
            when(this) {
                is Pixel -> {
                    if(sharedRegistry.enableShader && EnableHelper.canShader) {
                        this
                    } else {
                        this.spareStrategy
                    }
                }
                else -> this
            }
    }

    companion object {
        val Transparent = Color(androidx.compose.ui.graphics.Color.Transparent)
    }
}
