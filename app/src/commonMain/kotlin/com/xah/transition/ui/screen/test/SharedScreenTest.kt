package com.xah.transition.ui.screen.test

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xah.container.component.base.SharedContainer
import com.xah.container.component.base.SharedContent
import com.xah.container.component.overlay.SharedContainerRoot
import com.xah.container.util.LocalSharedRegistry
import com.sharednav.common.util.NoneRoundShape
import com.xah.transition.ui.component.APP_HORIZONTAL_DP
import com.xah.transition.ui.component.CARD_NORMAL_DP
import com.xah.transition.ui.component.SmallCard
import com.xah.transition.ui.component.TransplantListItem
import com.xah.transition.util.PlatformBackHandler
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun HomeScreenT(onPush : (Int) -> Unit) {
    val scrollState = rememberLazyGridState()
    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            state = scrollState,
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP- CARD_NORMAL_DP*2)
        ) {
            items(30, key = { it }) { index ->
                val route = "$index"
                Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                    SharedContainer(
                        key = route,
                        shape = MaterialTheme.shapes.small
                    ) {
                        SmallCard(
                            shape = NoneRoundShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            TransplantListItem(
                                headlineContent = { Text(route) },
                                modifier = Modifier.clickable {
                                    onPush(index)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecondScreenT(userId : Int,onBack : () -> Unit) {
    val route = "$userId"
    SharedContent(
        key = route
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Button(
                    onClick = {
                        onBack()
                    },
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Text("${userId} Back")
                }
            }
        }
    }
}

@Preview
@Composable
fun SharedTest() {
    var index by remember { mutableIntStateOf(-1) }
    var status by remember { mutableStateOf(true) }

    SharedContainerRoot {
        val registry = LocalSharedRegistry.current

        PlatformBackHandler(status == false) {
            registry.pop(index.toString()) {
                status = true
            }
        }

        AnimatedContent(
            transitionSpec = { fadeIn(registry.getPushAnimation()) togetherWith fadeOut(registry.getPopAnimation()) },
            targetState = status
        ) { s ->
            if(s) {
                HomeScreenT {
                    index = it
                    registry.push(index.toString()) {
                        status = false
                    }
                }
            } else {
                SecondScreenT(userId = index) {
                    registry.pop(index.toString()) {
                        status = true
                    }
                }
            }
        }
    }
}
