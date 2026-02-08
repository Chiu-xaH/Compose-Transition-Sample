package com.xah.transition.ui.screen.test

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.xah.navigation.state.LocalAnimatedContentScope
import com.xah.navigation.state.LocalSharedTransitionScope
import com.xah.transition.ui.component.APP_HORIZONTAL_DP
import com.xah.transition.ui.component.CARD_NORMAL_DP
import com.xah.transition.ui.component.SmallCard
import com.xah.transition.ui.component.TransplantListItem
import kotlinx.coroutines.launch

@Composable
fun HomeScreenT(onPush : (Int) -> Unit) {
    val scrollState = rememberLazyGridState()

    Box(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            state = scrollState,
            columns = GridCells.Fixed(2),
            modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP- CARD_NORMAL_DP*2)
        ) {
            items(30) { index ->
                val route = "Item #$index"
                SmallCard(
                    modifier = Modifier
                        .padding(CARD_NORMAL_DP*2),
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

@Composable
fun SecondScreenT(userId : Int,onBack : () -> Unit) {
    val route = "Item #$userId"
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Button(
                onClick = onBack,
                modifier = Modifier.align(Alignment.Center)
            ) {
                Text("${userId} Back")
            }
        }
    }
}

@Preview
@Composable
fun ShareTest() {
    var index by remember { mutableIntStateOf(-1) }
    var status by remember { mutableStateOf(true) }
    BackHandler(status == false) {
        status = true
    }
    SharedTransitionLayout {
        AnimatedContent(
            transitionSpec = {
                scaleIn(initialScale = 1f) togetherWith scaleOut(targetScale = 1f)
            },
            targetState = status
        ) { s ->
            CompositionLocalProvider(
                LocalSharedTransitionScope provides this@SharedTransitionLayout,
                LocalAnimatedContentScope provides this@AnimatedContent,
            ) {
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
}


@Composable
@Preview
fun ContainerTest() {
    val scope = rememberCoroutineScope()
//    val controller = rememberSharedContainerController(SpringContainerAnimation(stiffness = 100f))

//    val collapsedSpec = ContainerVisualState(
//        cornerRadius = 24.dp,
//        color = Color.Blue
//    )
//
//    val expandedSpec = ContainerVisualState(
//        cornerRadius = 0.dp,
//        color = Color.Green
//    )
//
//    Box() {
//        SharedContainerMask(controller.progress,collapsedSpec,expandedSpec)
//
//    }

    Column {
//        Box(
//            modifier = Modifier.background(Color.Red)
//        ) {
//        }
//        Box(
//            modifier = Modifier
//                .height(200.dp)
//                .scale(controller.progress)
//                .background(Color.Red)
//        ) {
//            Text("progress = ${"%.2f".format(controller.progress)}", modifier = Modifier.align(Alignment.Center))
//        }
//        Text("progress = ${"%.2f".format(controller.progress)}")
//
//        Button(onClick = {
//            scope.launch {
//                controller.expand()
//            }
//        }) {
//            Text("展开")
//        }
//
//        Button(onClick = {
//            scope.launch {
//                controller.collapse()
//            }
//        }) {
//            Text("收起")
//        }
//
    }
}
