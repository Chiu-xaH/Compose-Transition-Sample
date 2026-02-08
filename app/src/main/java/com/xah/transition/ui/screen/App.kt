package com.xah.transition.ui.screen

import android.os.Build
import android.view.RoundedCorner
import android.view.View
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.xah.container.SharedContainer
import com.xah.container.SharedContainerRoot
import com.xah.navigation.component.TransitionNavHost
import com.xah.navigation.model.NavActionState
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

@Composable
fun App() {
    val view = LocalView.current
    LaunchedEffect(Unit) {
        ScreenCornerHelper(view)
    }
    val nav = remember { NavStackState(startDestination = HomeDestination) }

    SharedContainerRoot {
        TransitionNavHost(
            state = nav,
//            sharedContainerKeyForEntry = { entry ->
//                (entry.destination as? SecondDestination)?.let { "Item #${it.userId}" }
//            }
        )
    }
}

@Composable
fun HomeScreen() {
    val navStackState = LocalNavStackState.current
    val scrollState = rememberLazyGridState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            state = scrollState,
            columns = GridCells.Fixed(2),
//            contentPadding = PaddingValues(CARD_NORMAL_DP*2),
            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP- CARD_NORMAL_DP*2)
        ) {
            items(30) { index ->
                val route = "Item #$index"
                Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                    SharedContainer (
                        key = route,
                        screenKey = "Home",
                        color = MaterialTheme.colorScheme.primaryContainer,
                        cornerRadius = 8.dp,
//                    elevation = 2.dp,
//                    transitionSpec = MaterialFadeInTransitionSpec
                    ) {
                        SmallCard(
                            modifier = Modifier
//                            .padding(CARD_NORMAL_DP*2)
//                        .containerShare(route)
                            ,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            TransplantListItem(
                                headlineContent = { Text(route) },
                                modifier = Modifier.clickable {
                                    navStackState.navigate(NavCommand.Push(SecondDestination(userId = index)))
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
    val density = LocalDensity.current

    SharedContainer(
        key = route,
        cornerRadius = if(navStackState.currentAction == NavActionState.NONE) 0.dp else with(density) {
            ScreenCornerHelper.corner.toDp()
        },
        color = MaterialTheme.colorScheme.surface,
        screenKey = "Second",
        isFullscreen = true,
    ) {
        Box(
            modifier = Modifier
//            .containerShare(route)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Button(
                onClick = {
                    navStackState.navigate(NavCommand.Push(ThirdDestination))
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

class ScreenCornerHelper(view : View) {
    companion object {
        var corner : Int = 0
            private set
    }

    init {
        corner = view.getScreenRoundCorner()
    }

    private fun View.getScreenRoundCorner() : Int {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            return 0
        } else {
            val insets = rootWindowInsets ?: return 0
            return insets.getRoundedCorner(RoundedCorner.POSITION_TOP_LEFT)?.radius ?: 0
        }
    }
}

