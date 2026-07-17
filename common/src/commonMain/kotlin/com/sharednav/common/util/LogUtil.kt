package com.sharednav.common.util

expect object LogUtil {
    fun init(tagName: String, debug: Boolean)
    fun verbose(msg: String = "")
    fun info(msg: String = "")
    fun debug(msg: String = "")
    fun warn(msg: String = "")
    fun error(msg: String = "")
    fun error(throwable: Throwable, msg: String = "")
}
