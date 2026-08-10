package com.xah.container.model

sealed interface ContentStrategy {
    /**
     * 全屏界面 导航转场（container不会在展开后隐藏，因为在全屏状态下无论是否隐藏都没有必要，content正常插值过渡）
     */
    data object Navigation : ContentStrategy

    /**
     * container会在展开后隐藏，content正常插值过渡
     * @param isFloating 是否为浮窗，浮窗为true，如果用于普通的同级界面中两个容器变换，为false
     */
    data class Layer(val isFloating : Boolean) : ContentStrategy

    /**
     * container不会隐藏也不会运动，content从container克隆出来过渡
     */
    data object Copy : ContentStrategy
}
