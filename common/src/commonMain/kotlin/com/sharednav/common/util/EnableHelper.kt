package com.sharednav.common.util

expect object EnableHelper {
    val canShader : Boolean
    val canBlur : Boolean
    val canPredictedGesture : Boolean
}