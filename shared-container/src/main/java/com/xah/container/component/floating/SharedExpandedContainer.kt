package com.xah.container.component.floating

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.xah.container.util.LocalSharedRegistry

@Composable
fun SharedExpandedContainer(
    key : String,
    expand : Boolean,
    modifier: Modifier = Modifier,
    container : @Composable () -> Unit,
    content : @Composable () -> Unit,
    onClosed : (Boolean) -> Unit,
) {
    val registry = LocalSharedRegistry.current

    BackHandler {
        registry.pop(key) {
            onClosed(false)
        }
    }

    AnimatedContent(
        modifier = modifier,
        targetState = expand,
        transitionSpec = { fadeIn(registry.getPushAnimation()) togetherWith fadeOut(registry.getPopAnimation()) },
    ) { isExpanded ->
        if(isExpanded) {
            content()
        } else {
            container()
        }
    }
}
