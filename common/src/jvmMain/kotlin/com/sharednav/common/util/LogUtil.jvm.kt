package com.sharednav.common.util

import java.util.logging.Logger
import java.util.logging.Level

actual object LogUtil {
    private var logger = Logger.getLogger("SharedNav")
    private var isDebug = false
    private var stackIndex: Int? = null

    actual fun init(tagName: String, debug: Boolean) {
        logger = Logger.getLogger(tagName)
        isDebug = debug
    }

    actual fun verbose(msg: String) { if (isDebug) baseLog(Level.FINEST, msg) }
    actual fun info(msg: String)    { baseLog(Level.INFO, msg) }
    actual fun debug(msg: String)   { if (isDebug) baseLog(Level.FINE, msg) }
    actual fun warn(msg: String)    { baseLog(Level.WARNING, msg) }
    actual fun error(msg: String)   { baseLog(Level.SEVERE, msg) }
    actual fun error(throwable: Throwable, msg: String) { baseLog(Level.SEVERE, msg, throwable) }

    private fun findCaller(): StackTraceElement? {
        val stack = Thread.currentThread().stackTrace
        return try {
            if (stackIndex != null && stackIndex!! in stack.indices) {
                stack[stackIndex!!]
            } else {
                val element = stack.firstOrNull {
                    it.className != LogUtil::class.java.name &&
                            it.className != Thread::class.java.name
                }
                stackIndex = element?.let { stack.indexOf(it) }
                element
            }
        } catch (e: Exception) { null }
    }

    private fun buildMsg(msg: String): String {
        val e = findCaller()
        return if (e == null) msg
        else "(${e.fileName}:${e.lineNumber}) ${e.methodName}()${if (msg.isEmpty()) "" else " : $msg"}"
    }

    private fun baseLog(level: Level, msg: String, throwable: Throwable? = null) {
        val text = buildMsg(msg)
        if (throwable != null) logger.log(level, text, throwable)
        else logger.log(level, text)
    }
}