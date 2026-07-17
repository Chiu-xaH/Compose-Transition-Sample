package com.xah.floating.model.componment

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.floating.model.Window
import com.xah.floating.model.anim.ForegroundEffect

abstract class BottomDialog : Window() {

    override val key: String? = null

    override val animation : ForegroundEffect = ForegroundEffect(
        enter = slideInVertically(
            initialOffsetY = { fullWidth -> fullWidth },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioLowBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullWidth -> fullWidth },
        )
    )

    open val modifier : Modifier = Modifier

    @Composable
    override fun Layer() {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = modifier.then(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .clip(RoundedCornerShape(ScreenCornerHelper.corner))
                )
            ) {
                Content()
            }
        }
    }
}