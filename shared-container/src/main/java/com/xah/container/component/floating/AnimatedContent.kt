package com.xah.container.component.floating

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import com.xah.container.util.LocalSharedRegistrySafely

@Composable
fun AnimatedContent(
    display : Boolean,
    key : String,
    onClosed : () -> Unit,
    content : @Composable () -> Unit
) {
    val registry = LocalSharedRegistrySafely.current
    if(registry == null) {
        if(display) {
            BackHandler {
                onClosed()
            }
            content()
        }
    } else {
        AnimatedVisibility(
            visible = display,
            enter = fadeIn(registry.getPushAnimation()),
            exit = fadeOut(registry.getPopAnimation())
        ) {
            BackHandler {
                registry.pop(key) {
                    onClosed()
                }
            }
            content()
        }
    }
}