package com.xah.floating.model.componment

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
import androidx.compose.ui.unit.dp
import com.sharednav.common.helper.ScreenCornerHelper
import com.xah.floating.model.Window
import com.xah.floating.model.anim.ForegroundEffect

abstract class BottomSheet : Window() {

    override val key: String? = null

    override val animation : ForegroundEffect = ForegroundEffect(
        enter = slideInVertically(initialOffsetY = { fullWidth -> fullWidth }),
        exit = slideOutVertically(targetOffsetY = { fullWidth -> fullWidth })
    )

    open val modifier : Modifier = Modifier

    @Composable
    override fun Layer() {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = modifier.then(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(topStart = ScreenCornerHelper.corner, topEnd = ScreenCornerHelper.corner, bottomStart = 0.dp, bottomEnd = 0.dp))
                )
            ) {
                Content()
            }
        }
    }
}