package com.xah.navigation.model.anim

import androidx.compose.runtime.Immutable
import kotlin.math.abs

/**
 * @param reserved 为true时，从start->end->start变化值，呈二次函数变化（峰值为end）；为false时，从start->end变化值，呈一次线性函数变化
 */
@Immutable
data class EffectValue<T>(
    val start : T,
    val end : T,
    val reserved: Boolean = false
) {
    companion object {
        fun <T> const(value : T) = EffectValue(value,value,false)
    }
    fun getFinalProgress(progress : Float) : Float {
        return if(reserved) {
            1f - abs(2f * progress - 1f)
        } else {
            progress
        }
    }
}
