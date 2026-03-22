package com.xah.container.model

/**
 * 当前所处状态
 */
enum class StatePause {
    CONTAINER,
    CONTENT,
    TRANSITING,
    MEASURING_CONTAINER,
    MEASURING_CONTENT
}