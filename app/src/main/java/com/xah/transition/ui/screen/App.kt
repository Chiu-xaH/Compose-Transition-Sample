package com.xah.transition.ui.screen

import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.xah.common.ScreenCornerHelper
import com.xah.container.container.SharedContainer
import com.xah.container.utils.LocalSharedRegistry
import com.xah.navigation.anim.EffectLevel
import com.xah.navigation.component.SharedNavHost
import com.xah.navigation.model.action.ActionType
import com.xah.navigation.model.action.LaunchMode
import com.xah.navigation.utils.LocalNavController
import com.xah.navigation.utils.LocalNavDependencies
import com.xah.navigation.utils.rememberNavDependencies
import com.xah.transition.R
import com.xah.transition.model.AppIconBean
import com.xah.transition.ui.component.APP_HORIZONTAL_DP
import com.xah.transition.ui.component.CARD_NORMAL_DP
import com.xah.transition.ui.component.CardListItem
import com.xah.transition.ui.component.CustomSlider
import com.xah.transition.ui.component.SmallCard
import com.xah.transition.ui.component.TransplantListItem
import com.xah.transition.ui.component.cardNormalColor
import com.xah.transition.ui.screen.destination.HomeDestination
import com.xah.transition.ui.screen.destination.detail.AppIconDestination
import com.xah.transition.ui.screen.destination.detail.SecondDestination
import com.xah.transition.ui.screen.destination.detail.ThirdDestination
import com.xah.transition.ui.screen.destination.settings.BezierSettingsDestination
import com.xah.transition.ui.screen.destination.settings.CornerSettingsDestination
import com.xah.transition.ui.screen.test.CubicBezierEditor
import com.xah.transition.ui.style.topBarTransplantColor
import com.xah.transition.ui.viewmodel.UiHolder

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun App() {
    var arg1 by remember { mutableStateOf(1) }
    val deps = rememberNavDependencies(arg1) {
        put(arg1, tag = "args1")
        put("1", tag = "args2")
    }

    SharedNavHost(HomeDestination, modifier = Modifier.background(MaterialTheme.colorScheme.surface), dependencies = deps)
}

private val appList = listOf<AppIconBean>(
    AppIconBean("jd","京东",R.drawable.ic_jd),
    AppIconBean("xhs","小红书",R.drawable.ic_xhs),
    AppIconBean("amap","高德地图",R.drawable.ic_amap),
    AppIconBean("qweather","和风天气",R.drawable.ic_qweather),
    AppIconBean("iqiyi","爱奇艺",R.drawable.ic_iqiyi),
    AppIconBean("candy","Candy Crush Saga",R.drawable.ic_candy),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val q = LocalNavDependencies.current.get<Int>("args1")
    val q2 = LocalNavDependencies.current.get<String>("args2")

    val navController = LocalNavController.current
    val scrollState = rememberLazyGridState()
    val registry = LocalSharedRegistry.current
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
        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                SharedContainer (
                    containerColor = MaterialTheme.colorScheme.inversePrimary,
                    key = CornerSettingsDestination.key,
                    shape = FloatingActionButtonDefaults.shape
                ) {
                    FloatingActionButton(
                        elevation = FloatingActionButtonDefaults.elevation(0.dp,0.dp,0.dp,0.dp),
                        containerColor = MaterialTheme.colorScheme.inversePrimary,
                        shape = RoundedCornerShape(0.dp),
                        onClick = {
                            navController.push(CornerSettingsDestination)
                        }
                    ) {
                        Icon(painterResource(CornerSettingsDestination.icon),null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            },
        ) { innerPadding ->
            Box(modifier = Modifier.fillMaxSize()) {
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
                        val destination = AppIconDestination(item)
                        Column {
                            SharedContainer(
                                destination.key,
                                containerColor = null,
                                shape = RoundedCornerShape(20.dp),
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(150.dp)
                                        .clickable {
                                            navController.push(destination)
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
//                                containerFilledStrategy = ContainerFilledStrategy.Clip,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small,
                            ) {
                                Card(
                                    shape = RoundedCornerShape(0.dp),
//                                    modifier = Modifier.height(150.dp).width(46.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    TransplantListItem(
                                        headlineContent = { Text("Item #${index}") },
                                        modifier = Modifier.clickable {
                                            navController.push(SecondDestination(userId = index))
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
                            modifier = Modifier.padding(CARD_NORMAL_DP*2)
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
                        Surface(
                            color = cardNormalColor(),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(CARD_NORMAL_DP*2)
                        ) {
                            TransplantListItem(
                                headlineContent = {
                                    Text("双向填充")
                                },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.texture),null)
                                },
                                trailingContent = {
                                    Switch(registry.extensionDouble, onCheckedChange = { registry.extensionDouble = it })
                                },
                                modifier = Modifier.clickable {
                                    registry.extensionDouble = !registry.extensionDouble
                                },
                            )
                        }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Spacer(Modifier.navigationBarsPadding().height((APP_HORIZONTAL_DP+innerPadding.calculateBottomPadding())*3))
                    }
                }
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentSize provides 0.dp
                ) {
                    SharedContainer(
                        containerColor = MaterialTheme.colorScheme.inversePrimary,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(horizontal = APP_HORIZONTAL_DP)
                        ,
                        key = BezierSettingsDestination.key,
                        shape = CircleShape
                    ) {
                        FilledTonalIconButton (
                            shape = RoundedCornerShape(0.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(containerColor = MaterialTheme.colorScheme.inversePrimary),
                            onClick = {
                                navController.push(BezierSettingsDestination)
                            }
                        ) {
                            Icon(painterResource(BezierSettingsDestination.icon),null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SecondScreen() {
    val navController = LocalNavController.current
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

@Composable
fun AppIconScreen() {
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

@Composable
fun ThirdScreen() {
    val navController = LocalNavController.current
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
                navController.push(HomeDestination, LaunchMode.Single(reuse = true, actionType = ActionType.POP))
            },
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text("To Home")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CornerSettingsScreen(title : String) {
    val navController = LocalNavController.current
    val registry = LocalSharedRegistry.current
    val view = LocalView.current

    var corner by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        corner = ScreenCornerHelper.corner.value
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.inversePrimary,
        topBar = {
            MediumTopAppBar(
                colors = topBarTransplantColor(),
                title = { Text(title) },
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    ScreenCornerHelper.corner = corner.dp
                    navController.pop()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(APP_HORIZONTAL_DP)
                    .navigationBarsPadding()
            ) {
                Text("保存")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface,RoundedCornerShape(corner.dp))
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column (modifier = Modifier.align(Alignment.Center),horizontalAlignment = Alignment.CenterHorizontally) {
                CustomSlider(
                    value = corner,
                    onValueChange = {
                        corner = it
                    },
                    valueRange = 0f..100f
                )
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = APP_HORIZONTAL_DP)) {
                    FilledTonalButton(
                        onClick = {
                            corner -= 0.5f
                        },
                        enabled = corner > 0f,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Text("-0.5")
                    }
                    FilledTonalButton(
                        onClick = {
                            corner = ScreenCornerHelper(view).getCornerDp().value
                        },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        Text("$corner")
                    }
                    FilledTonalButton(
                        onClick = {
                            corner += 0.5f
                        },
                        enabled = corner < 100f,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Text("+0.5")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BezierSettingsScreen(title: String) {
    val registry = LocalSharedRegistry.current
    var isPush by rememberSaveable { mutableStateOf(true) }

    Scaffold(
        topBar = {
            MediumTopAppBar(
                colors = topBarTransplantColor(),
                title = { Text(title) },
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    isPush = !isPush
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(APP_HORIZONTAL_DP)
                    .navigationBarsPadding()
            ) {
                Text("当前调节${
                    if(isPush) {
                        "PUSH"
                    } else {
                        "POP"
                    }
                }")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if(isPush) {
                CubicBezierEditor(
                    registry.pushX1,
                    registry.pushY1,
                    registry.pushX2,
                    registry.pushY2,
                    { registry.pushX1 = it },
                    { registry.pushY1 = it },
                    { registry.pushX2 = it },
                    { registry.pushY2 = it },
                )
            } else {
                CubicBezierEditor(
                    registry.popX1,
                    registry.popY1,
                    registry.popX2,
                    registry.popY2,
                    { registry.popX1 = it },
                    { registry.popY1 = it },
                    { registry.popX2 = it },
                    { registry.popY2 = it },
                )
            }
        }
    }
}


