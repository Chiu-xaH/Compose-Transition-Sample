package com.xah.transition.ui.screen

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xah.container.ui.container.SharedContainer
import com.xah.container.ui.container.SharedContent
import com.xah.container.ui.overlay.SharedContainerRoot
import com.xah.container.ui.util.LocalSharedContainerRegistry
import com.xah.navigation.component.TransitionNavHost
import com.xah.navigation.model.NavCommand
import com.xah.navigation.state.LocalNavStackState
import com.xah.navigation.state.NavStackState
import com.xah.transition.ui.component.APP_HORIZONTAL_DP
import com.xah.transition.ui.component.CARD_NORMAL_DP
import com.xah.transition.ui.component.SmallCard
import com.xah.transition.ui.component.TransplantListItem
import com.xah.transition.ui.screen.destination.HomeDestination
import com.xah.transition.ui.screen.destination.SecondDestination
import com.xah.transition.ui.screen.destination.ThirdDestination
import kotlinx.coroutines.launch

@Composable
fun App() {
    val nav = remember { NavStackState(startDestination = HomeDestination) }

    SharedContainerRoot {
        TransitionNavHost(state = nav)
    }
}

@Composable
fun HomeScreen() {
    val navStackState = LocalNavStackState.current
    val scrollState = rememberLazyGridState()
    val scope = rememberCoroutineScope()
    val registry = LocalSharedContainerRegistry.current

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            state = scrollState,
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP- CARD_NORMAL_DP*2)
        ) {
            items(30) { index ->
                val route = "Item #$index"
                Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                    SharedContainer (
                        key = route,
                        fillColor = MaterialTheme.colorScheme.primaryContainer,
                        corner = 8.dp,
                    ) {
                        SmallCard(
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            TransplantListItem(
                                headlineContent = { Text(route) },
                                modifier = Modifier.clickable {
                                    scope.launch {
                                        registry.push(route) {
                                            navStackState.navigate(NavCommand.Push(SecondDestination(userId = index)))
                                        }
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
fun SecondScreen(userId : Int) {
    val navStackState = LocalNavStackState.current
    val route = "Item #$userId"
    val scope = rememberCoroutineScope()
    val registry = LocalSharedContainerRegistry.current

    SharedContent (
        key = route,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Button(
                onClick = {
                    scope.launch {
                        registry.pop(route) {
                            navStackState.navigate(NavCommand.Pop)
                        }
                    }
                },
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("$userId to ThirdScreen")
            }
        }
    }
}



@Composable
fun ThirdScreen() {
    val navStackState = LocalNavStackState.current
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.primaryContainer)) {
        Button(
            onClick = {
                navStackState.navigate(NavCommand.Pop)
            },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("Back to")
        }
    }
}


