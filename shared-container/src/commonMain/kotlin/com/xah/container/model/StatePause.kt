package com.xah.container.model

/**
 * 当前所处状态
 */
enum class StatePause {
    CONTAINER,
    CONTENT,
    TRANSITING_TO_CONTENT,
    TRANSITING_TO_CONTAINER,
    MEASURING_CONTAINER,
    MEASURING_CONTENT;

    internal fun isTransiting() = this == TRANSITING_TO_CONTAINER || this == TRANSITING_TO_CONTENT

    /**
     * 对立面转换
     */
    internal fun getTargetTransiting() = when(this) {
        CONTENT -> TRANSITING_TO_CONTAINER
        CONTAINER -> TRANSITING_TO_CONTENT
        MEASURING_CONTENT -> TRANSITING_TO_CONTENT
        MEASURING_CONTAINER -> TRANSITING_TO_CONTAINER
        TRANSITING_TO_CONTENT -> TRANSITING_TO_CONTAINER
        TRANSITING_TO_CONTAINER -> TRANSITING_TO_CONTENT
    }
}