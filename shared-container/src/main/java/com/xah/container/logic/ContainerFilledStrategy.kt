package com.xah.container.logic

import android.os.Build
import com.xah.container.logic.model.SharedContainerState

sealed interface ContainerFilledStrategy {
    /**
     * 指定颜色填充底部
     */
    data class Color(val color : androidx.compose.ui.graphics.Color) : ContainerFilledStrategy

    /**
     * 自动取底部1像素做填充（类似OriginOS 6.0）
     * Require SDK33+ 若低版本使用此效果则降级为spareStrategy
     */
    data class Pixel(val spareStrategy : ContainerFilledStrategy = Clip) : ContainerFilledStrategy

    /**
     * 裁切放大（类似OriginOS 1.0）
     */
    data object Clip : ContainerFilledStrategy

    fun getFinalStrategy() : ContainerFilledStrategy = when(this) {
        is Pixel -> {
            if(CAN_USE_SHADER) {
                this
            } else {
                this.spareStrategy
            }
        }
        else -> this
    }

    companion object {
        val CAN_USE_SHADER = Build.VERSION.SDK_INT >= 33
    }
}
