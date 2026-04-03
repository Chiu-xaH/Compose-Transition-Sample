package com.xah.container.model

sealed interface ContentStrategy {
    /**
     * 全屏界面 导航转场（container不会在展开后隐藏，因为在全屏状态下无论是否隐藏都没有必要，content正常插值过渡）
     */
    data object Navigation : ContentStrategy

    /**
     * 浮窗（container会在展开后隐藏，content正常插值过渡）
     */
    data object FloatingWindow : ContentStrategy

    /**
     * container不会隐藏也不会运动，content以透明开始从container出来过渡
     */
    data object Reveal : ContentStrategy
}
