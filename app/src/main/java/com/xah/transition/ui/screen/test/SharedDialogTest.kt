package com.xah.transition.ui.screen.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xah.container.container.SharedContainer
import com.xah.container.container.SharedContent
import com.xah.container.container.SharedExpandedContainer
import com.xah.container.overlay.SharedContainerRoot
import com.xah.container.util.LocalSharedRegistry
import com.xah.container.util.LocalSharedRegistrySafely
import com.xah.transition.R
import com.xah.transition.ui.component.APP_HORIZONTAL_DP

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
                    isFullScreen = false,
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
                        shape = RoundedCornerShape(0.dp)
                    ) {
                        Image(
                            painterResource(R.drawable.ic_jd),
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