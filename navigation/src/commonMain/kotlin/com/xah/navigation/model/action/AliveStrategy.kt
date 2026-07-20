package com.xah.navigation.model.action

/**
 * 非栈顶页面的存活策略
 */
enum class AliveStrategy {
    /**
     * 保留页面真正的不被销毁,页面还在，只不过被盖住了,可节省POP的性能开销（!!!多页面卡顿OOM警告,不建议启用）
     */
    KEEP_ALIVE,
    /**
     * 保留页面的State通过SaveableStateHolder,页面被销毁，但变量等被保存下来，再次重建页面时放回变量,可节省多余性能开销，但每次POP需要一些性能去重建页面。
     */
    SAVE_STATE,
    /**
     * 不保存
     */
    NONE
}