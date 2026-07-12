package com.xah.navigation.model.anim

enum class EffectLevel(val levelNum : Int) {
    /**
     * 满效果
     */
    HIGH(3),
    /**
     * 去掉模糊，可自行重写
     */
    MEDIUM(2),
    /**
     * 去掉模糊与缩放，可自行重写
     */
    LOW(1),
    /**
     * 走NavigationController里最低级的统一效果
     */
    NONE(0)
}