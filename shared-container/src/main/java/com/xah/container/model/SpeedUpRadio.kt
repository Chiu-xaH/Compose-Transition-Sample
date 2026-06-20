package com.xah.container.model

data class SpeedUpRadio(
    val alpha : Float,
    val corner : Float,
    val tilt : Float
) {
    companion object {
        val default = SpeedUpRadio(1.5f,1.5f,1.25f)
    }
}