package com.xah.transition.ui.screen

import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.xah.common.LogUtil
import com.xah.container.container.SharedContainer
import com.xah.container.container.SharedContent
import com.xah.container.container.bottomExtension
import com.xah.container.model.ContainerFilledStrategy
import com.xah.container.utils.LocalSharedContainerRegistry
import com.xah.navigation.anim.EffectLevel
import com.xah.navigation.shared.SharedNavHelper
import com.xah.navigation.component.SharedNavHost
import com.xah.navigation.utils.LocalNavigationController
import com.xah.navigation.utils.LocalNavigationDestination
import com.xah.transition.R
import com.xah.transition.ui.component.APP_HORIZONTAL_DP
import com.xah.transition.ui.component.CARD_NORMAL_DP
import com.xah.transition.ui.component.CardListItem
import com.xah.transition.ui.component.SmallCard
import com.xah.transition.ui.component.TransplantListItem
import com.xah.transition.ui.screen.destination.AppHomeDestination
import com.xah.transition.ui.screen.destination.HomeDestination
import com.xah.transition.ui.screen.destination.SecondDestination
import com.xah.transition.ui.screen.destination.SettingsDestination
import com.xah.transition.ui.screen.destination.ThirdDestination
import com.xah.transition.ui.style.topBarTransplantColor
import com.xah.transition.ui.viewmodel.UiHolder

@Composable
fun App() {
    SharedNavHost(HomeDestination)
}

data class AppBean(
    val key : String,
    val name : String,
    val icon : Int
)

private val appList = listOf<AppBean>(
    AppBean("jd","京东",R.drawable.ic_jd),
    AppBean("xhs","小红书",R.drawable.ic_xhs),
    AppBean("amap","高德地图",R.drawable.ic_amap),
    AppBean("qweather","和风天气",R.drawable.ic_qweather),
    AppBean("iqiyi","爱奇艺",R.drawable.ic_iqiyi),
    AppBean("candy","Candy Crush Saga",R.drawable.ic_candy),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val navController = LocalNavigationController.current
    val scrollState = rememberLazyGridState()
    val registry = LocalSharedContainerRegistry.current
    val context = LocalContext.current
    val pickMediaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { imageUri ->
            // 使用 context 读取图片为 ImageBitmap
            UiHolder.imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        }
    }

    val levelList = remember { EffectLevel.entries }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if(UiHolder.imageBitmap != null) {
            Image(
                bitmap = UiHolder.imageBitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    scrollBehavior = scrollBehavior,
                    colors = topBarTransplantColor(),
                    title = { Text("SharedNav") },
                    actions = {
                        val dest = SettingsDestination
                        CompositionLocalProvider(
                            LocalMinimumInteractiveComponentSize provides 0.dp
                        ) {
                            SharedContainer(
                                containerFilledStrategy = ContainerFilledStrategy.Color(MaterialTheme.colorScheme.primaryContainer),
                                modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP),
                                shadow = CARD_NORMAL_DP*5,
                                key = dest.key,
                                corner = CircleShape
                            ) {
                                FilledTonalIconButton (
                                    colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                    onClick = {
                                        SharedNavHelper.push(dest,navController,registry)
                                    }
                                ) {
                                    Icon(painterResource(R.drawable.settings),null, tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyVerticalGrid(
                state = scrollState,
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP- CARD_NORMAL_DP*2)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(Modifier.height(APP_HORIZONTAL_DP+innerPadding.calculateTopPadding()))
                }
                items(appList.size,key = { appList[it].key }) { index ->
                    val item = appList[index]
                    val destination = AppHomeDestination(item)
                    Column {
                        SharedContainer(
                            destination.key,
                            containerFilledStrategy = ContainerFilledStrategy.Pixel(),
                            corner = RoundedCornerShape(20.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clickable {
                                        SharedNavHelper.push(destination,navController,registry)
                                    }
                            ) {
                                Image(painterResource(item.icon),null)
                            }
                        }
                        Spacer(Modifier.height(APP_HORIZONTAL_DP*2))
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(Modifier.height(APP_HORIZONTAL_DP))
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = CARD_NORMAL_DP*2, vertical = CARD_NORMAL_DP)
                    ) {
                        TransplantListItem(
                            headlineContent = {
                                Text("设置壁纸")
                            },
                            modifier = Modifier.clickable {
                                pickMediaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            }
                        )
                    }
                }
                items(30) { index ->
                    val destination = SecondDestination(userId = index)
                    Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                        SharedContainer (
                            key = destination.key,
                            containerFilledStrategy =
//                                ContainerFilledStrategy.Clip
                            ContainerFilledStrategy.Color(MaterialTheme.colorScheme.primaryContainer)
                            ,
                            corner = MaterialTheme.shapes.small,
                        ) {
                            SmallCard(
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                TransplantListItem(
                                    headlineContent = { Text("Item #${index}") },
                                    modifier = Modifier.clickable {
                                        SharedNavHelper.push(SecondDestination(userId = index),navController,registry)
                                    }
                                )
                            }
                        }
                    }
                }
                items(10) { index ->
                    val route = "ItemNo #$index"
                    Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                        SmallCard(
                            modifier = Modifier,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            TransplantListItem(
                                headlineContent = { Text(route) },
                                modifier = Modifier.clickable {
                                    navController.push(ThirdDestination)
                                }
                            )
                        }
                    }
                }
                items(levelList.size, key = { levelList[it].levelNum }) { index ->
                    val item = levelList[index]
                    val selected = navController.transitionLevel == item

                    val color = if(selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer

                    Surface(
                        color = color,
                        shape = MaterialTheme.shapes.small,
                        modifier = Modifier.padding(horizontal = CARD_NORMAL_DP*2, vertical = CARD_NORMAL_DP)
                    ) {
                        TransplantListItem(
                            headlineContent = {
                                Text("等级 ${item.name}", color = contentColorFor(color))
                            },
                            modifier = Modifier.clickable {
                                navController.transitionLevel = item
                            },
                        )
                    }
                }
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Spacer(Modifier.navigationBarsPadding().height(APP_HORIZONTAL_DP+innerPadding.calculateBottomPadding()))
                }
            }
        }
    }
}

@Composable
fun SecondScreen() {
    val navController = LocalNavigationController.current
    val destination = LocalNavigationDestination.current
    SharedContent (
        key = destination.key,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn {
                items(30) {
                    CardListItem(
                        headlineContent = {
                            Text("测试$it")
                        },
                        modifier = Modifier.clickable {
                            navController.push(ThirdDestination)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AppHomeScreen() {
    val destination = LocalNavigationDestination.current
    SharedContent (
        key = destination.key,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn {
                items(30) {
                    CardListItem(
                        headlineContent = {
                            Text("测试$it")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ThirdScreen() {
    val navController = LocalNavigationController.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
            .clickable {
                navController.pop()
            }
    ) {
        Button(
            onClick = {
                navController.home()
            },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("To Home")
        }
    }
}

@Composable
fun SettingsScreen() {
    val destination = LocalNavigationDestination.current

    SharedContent (
        key = destination.key,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn {
                items(30) {
                    CardListItem(
                        headlineContent = {
                            Text("测试$it")
                        }
                    )
                }
            }
        }
    }
}


@Composable
@Preview
fun Test() {
    var rect by remember { mutableStateOf<Rect?>(null) }
    var rect2 by remember { mutableStateOf<Rect?>(null) }
    val graphicsLayer = rememberGraphicsLayer()
    val graphicsLayer2 = rememberGraphicsLayer()

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    val position = coordinates.positionInRoot()
                    val size = coordinates.size

                    rect = Rect(
                        left = position.x,
                        top = position.y,
                        right = position.x + size.width,
                        bottom = position.y + size.height
                    )
                }
                .drawWithContent {
//                    drawContent()
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                }
        ) {
            Image(painterResource(R.drawable.ic_candy),null)
        }
        Box(modifier = Modifier.align(Alignment.Center)) {
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        val position = coordinates.positionInRoot()
                        val size = coordinates.size

                        rect2 = Rect(
                            left = position.x,
                            top = position.y,
                            right = position.x + size.width,
                            bottom = position.y + size.height
                        )
                    }
                    .drawWithContent {
//                        drawContent()
                        graphicsLayer2.record {
                            this@drawWithContent.drawContent()
                        }
                    }
            ) {
                if(rect2 != null && rect != null) {
                    LogUtil.debug("rect=$rect rect2=$rect2")
                    Box(
                        modifier = Modifier.drawWithContent {
                            withTransform({
                                translate(
                                    left = rect!!.left,
                                    top = rect!!.top,
//                                    left = (rect2!!.left-rect2!!.left)/2
                                )
                            }) {
                                drawLayer(graphicsLayer)
                            }
                        }
                    )
                }
//                Image(painterResource(R.drawable.ic_candy),null)
            }
            Box(
                modifier = Modifier
                    .zIndex(-1f)
                    .bottomExtension(graphicsLayer2,rect2)
            )
        }
    }
}