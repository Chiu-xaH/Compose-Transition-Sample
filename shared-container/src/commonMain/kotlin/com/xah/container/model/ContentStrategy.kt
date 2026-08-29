package com.xah.container.model

sealed class ContentStrategy(
    open val enableContainerAlpha : Boolean = false,
    open val enableContentAlpha : Boolean = true
) {
    /**
     * 保留接口：专门用于全屏界面的导航转场
     * @param enableContainerAlpha 允许container做透明度变化
     */
    data class Navigation(
        override val enableContainerAlpha : Boolean = false,
    ) : ContentStrategy(enableContainerAlpha,true)

    /**
     * 适用于正常情况下两个容器之间的过渡
     *
     * 后续会在开发文档里附图文对比
     * @param keepShowContainer 是否需要保持container不隐藏
     * @param enableContainerAlpha 允许container做透明度变化
     */
    data class Shared(
        val keepShowContainer : Boolean,
        override val enableContainerAlpha : Boolean = false,
    ) : ContentStrategy(enableContainerAlpha,true)
}
