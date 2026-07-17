package com.sharednav.common.util

import platform.Foundation.NSLog

actual object LogUtil {

    private var tag = "SharedNav"
    private var isDebug = false

    actual fun init(tagName: String, debug: Boolean) {
        tag = tagName
        isDebug = debug
    }

    actual fun verbose(msg: String) {
        if (isDebug) log("VERBOSE", msg)
    }

    actual fun info(msg: String) {
        log("INFO", msg)
    }

    actual fun debug(msg: String) {
        if (isDebug) log("DEBUG", msg)
    }

    actual fun warn(msg: String) {
        log("WARN", msg)
    }

    actual fun error(msg: String) {
        log("ERROR", msg)
    }

    actual fun error(throwable: Throwable, msg: String) {
        log("ERROR", "$msg\n${throwable.stackTraceToString()}")
    }

    private fun log(level: String, msg: String) {
        NSLog("[$tag][$level] $msg")
    }
}