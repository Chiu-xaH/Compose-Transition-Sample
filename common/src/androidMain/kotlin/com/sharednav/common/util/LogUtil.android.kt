package com.sharednav.common.util

import android.util.Log
import com.xah.sharednav.common.BuildConfig


actual object LogUtil {
    private var tag: String = "SharedNav"
    // 找到后为stackIndex赋值，以后再用就不用遍历了
    private var stackIndex : Int? = null

    private enum class Level(val priority: Int) {
        NONE(-1),
        VERBOSE(0),
        DEBUG(1),
        INFO(2),
        WARN(3),
        ERROR(4)
    }

    actual fun init(tagName : String, debug : Boolean) {
        tag = tagName
        if(debug) {
            minLevel = Level.VERBOSE
        } else {
            if (BuildConfig.DEBUG) Level.VERBOSE else Level.WARN
        }
    }

    private var minLevel: Level = if (BuildConfig.DEBUG) Level.VERBOSE else Level.WARN

    actual fun verbose(msg : String) = baseLog(Level.VERBOSE,msg)
    actual fun info(msg : String) = baseLog(Level.INFO,msg)
    actual fun debug(msg : String) = baseLog(Level.DEBUG,msg)
    actual fun warn(msg : String) = baseLog(Level.WARN,msg)
    actual fun error(throwable: Throwable,msg : String) = baseLog(Level.ERROR,msg,throwable)
    actual fun error(msg : String) = baseLog(Level.ERROR,msg)

    private fun findCaller(): StackTraceElement? {
        val stack = Thread.currentThread().stackTrace
        return try {
            if (stackIndex != null && stackIndex!! in stack.indices) {
                stack[stackIndex!!]
            } else {
                val element = stack.firstOrNull { element ->
                    element.className != this::class.java.name && element.className != Thread::class.java.name && element.className != "dalvik.system.VMStack"
                }
                stackIndex = element?.let { stack.indexOf(it) }
                element
            }
        } catch (e : Exception) {
            error(e)
            null
        }
    }

    private fun baseLog(type: Level,msg : String, throwable: Throwable? = null) {
        if (type.priority < minLevel.priority) {
            return
        }

        val element = findCaller()
        val text = if(element == null) msg else "(${element.fileName}:${element.lineNumber}) ${element.methodName}()${if(msg.isEmpty()) "" else " : $msg"}"
        when(type) {
            Level.NONE -> return
            Level.VERBOSE -> Log.v(tag,text)
            Level.INFO -> Log.i(tag,text)
            Level.DEBUG -> Log.d(tag,text)
            Level.WARN -> Log.w(tag,text)
            Level.ERROR -> {
                if(throwable != null) {
                    Log.e(tag,text,throwable)
                } else {
                    Log.e(tag,text)
                }
            }
        }
    }
}