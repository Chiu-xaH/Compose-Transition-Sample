package com.xah.navigation.model.anim

import androidx.compose.animation.core.AnimationSpec
import com.xah.navigation.model.anim.effect.PageEffects

interface TransitionEffect {
    val pageEffect: PageEffects
    val predictiveMinValue: Float
    val pushAnimation: AnimationSpec<Float>
    val popAnimation: AnimationSpec<Float>
}