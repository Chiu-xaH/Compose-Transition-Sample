package com.xah.transition.ui.screen.test

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xah.container.container.SharedContainer
import com.xah.container.container.SharedContent
import com.xah.container.overlay.SharedContainerRoot
import com.xah.container.utils.LocalSharedContainerRegistry
import com.xah.transition.ui.component.APP_HORIZONTAL_DP
import com.xah.transition.ui.component.CARD_NORMAL_DP
import com.xah.transition.ui.component.SmallCard
import com.xah.transition.ui.component.TransplantListItem

@Composable
fun HomeScreenT(onPush : (Int) -> Unit) {
    val registry = LocalSharedContainerRegistry.current
    val scrollState = rememberLazyGridState()
    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            state = scrollState,
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP- CARD_NORMAL_DP*2)
        ) {
            items(30, key = { it }) { index ->
                val route = "Item #$index"
                Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                    SharedContainer(
                        key = route,
                        corner = MaterialTheme.shapes.small
                    ) {
                        SmallCard(
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            TransplantListItem(
                                headlineContent = { Text(route) },
                                modifier = Modifier.clickable {
                                    registry.push(route) {
                                        onPush(index)
                                    }
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
    val registry = LocalSharedContainerRegistry.current
    val route = "Item #$userId"
    SharedContent(
        key = route
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Button(
                    onClick = {
                        registry.pop(route) {
                            onBack()
                        }
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
    BackHandler(status == false) {
        status = true
    }

    SharedContainerRoot {
        AnimatedContent(
            transitionSpec = {
                scaleIn(initialScale = 1f) togetherWith scaleOut(targetScale = 1f)
            },
            targetState = status
        ) { s ->
            if(s) {
                HomeScreenT {
                    index = it
                    status = false
                }
            } else {
                SecondScreenT(userId = index) {
                    status = true
                }
            }
        }
    }
}



