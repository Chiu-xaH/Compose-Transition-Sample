package com.xah.transition.util

import com.sharednav.common.util.LogUtil
import kotlin.math.pow
import kotlin.math.round

fun Float.roundOff(weiShu: Int): Float {
    return try {
        val factor = 10.0.pow(weiShu)
        (round(this.toDouble() * factor) / factor).toFloat()
    } catch (e: Exception) {
        LogUtil.error(e)
        0f
    }
}

fun Double.roundOff(weiShu: Int): Double {
    return try {
        val factor = 10.0.pow(weiShu)
        round(this * factor) / factor
    } catch (e: Exception) {
        LogUtil.error(e)
        0.0
    }
}

fun Float.roundOffString(weiShu: Int): String =
    this.toDouble().roundOffString(weiShu)

fun Double.roundOffString(weiShu: Int): String {
    return try {
        val rounded = roundOff(weiShu)
        if (weiShu == 0) rounded.toLong().toString()
        else formatDecimal(rounded, weiShu)
    } catch (e: Exception) {
        LogUtil.error(e)
        "0"
    }
}

private fun formatDecimal(value: Double, weiShu: Int): String {
    val s = value.toString()
    val dot = s.indexOf('.')
    return when {
        dot == -1 -> "$s." + "0".repeat(weiShu)
        s.substring(dot + 1).length >= weiShu ->
            s.substring(0, dot + 1 + weiShu)
        else -> s + "0".repeat(weiShu - (s.length - dot - 1))
    }
}