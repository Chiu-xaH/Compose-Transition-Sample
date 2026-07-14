package com.xah.transition.ui.screen.test

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sharednav.common.helper.NoneRoundShape
import com.xah.container.component.base.SharedContainer
import com.xah.container.component.base.SharedContent
import com.xah.container.component.overlay.SharedContainerRoot
import com.xah.container.model.ContentStrategy
import com.xah.container.util.LocalSharedRegistry
import com.xah.container.util.LocalSharedRegistrySafely
import com.xah.transition.ui.component.APP_HORIZONTAL_DP
import com.xah.transition.util.PlatformBackHandler
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import sharednav.app.generated.resources.Res
import sharednav.app.generated.resources.ic_jd

@Preview
@Composable
fun SharedDialogTest() {
    var displayDetail by remember { mutableStateOf(false) }
    val key = remember { "key" }

    @Composable
    fun UI(modifier: Modifier = Modifier) {
        val registry = LocalSharedRegistry.current
        SharedExpandedContainer(
            modifier = modifier,
            expand = displayDetail,
            key = key,
            content = {
                SharedContent(
                    key = key,
                    shape = MaterialTheme.shapes.large,
                    contentStrategy = ContentStrategy.Layer(isFloating = true),
                    modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = 180.dp, height = 320.dp)
                                .clickable {
                                    registry.pop(key) {
                                        displayDetail = false
                                    }
                                }
                        ) {
                            Text(key, modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
            },
            container = {
                SharedContainer (
                    key = key,
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = NoneRoundShape
                    ) {
                        Image(
                            painterResource(Res.drawable.ic_jd),
                            null,
                            modifier = Modifier
                                .size(100.dp)
                                .clickable {
                                    registry.push(key) {
                                        displayDetail = true
                                    }
                                }
                        )
                    }
                }
            }
        ) { displayDetail = it }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val topRegistry = LocalSharedRegistrySafely.current
        if(topRegistry == null) {
            SharedContainerRoot { UI() }
        } else {
            UI()
        }
    }
}

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

    PlatformBackHandler {
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