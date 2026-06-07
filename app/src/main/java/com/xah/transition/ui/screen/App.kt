package com.xah.transition.ui.screen

import android.provider.MediaStore
import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.util.lerp
import com.sharednav.common.helper.ScreenCornerHelper
import com.sharednav.common.util.NoneRoundShape
import com.xah.container.component.base.SharedContainer
import com.xah.container.model.ContainerFilledStrategy
import com.xah.container.util.LocalSharedRegistry
import com.xah.floating.util.LocalFloatingController
import com.xah.navigation.anim.effect.Direction
import com.xah.navigation.anim.effect.FadeTransitionEffect
import com.xah.navigation.anim.effect.FlipTransitionEffect
import com.xah.navigation.anim.effect.IslandTransitionEffect
import com.xah.navigation.anim.effect.JumpTransitionEffect
import com.xah.navigation.anim.effect.ScaleTransitionEffect
import com.xah.navigation.anim.effect.SlideTransitionEffect
import com.xah.navigation.component.SharedNavHost
import com.xah.navigation.component.rememberNavController
import com.xah.navigation.model.action.ActionType
import com.xah.navigation.model.action.LaunchMode
import com.xah.navigation.model.anim.EffectLevel
import com.xah.navigation.model.anim.effect.sub.Rotation
import com.xah.navigation.model.dest.Destination
import com.xah.navigation.util.LocalNavController
import com.xah.navigation.util.LocalNavDependencies
import com.xah.navigation.util.rememberNavDependencies
import com.xah.transition.R
import com.xah.transition.model.AppIconBean
import com.xah.transition.ui.component.APP_HORIZONTAL_DP
import com.xah.transition.ui.component.CARD_NORMAL_DP
import com.xah.transition.ui.component.CustomCard
import com.xah.transition.ui.component.CustomSlider
import com.xah.transition.ui.component.DividerTextExpandedWithShared
import com.xah.transition.ui.component.TopBarNavigationIcon
import com.xah.transition.ui.component.TransplantListItem
import com.xah.transition.ui.component.cardNormalColor
import com.xah.transition.ui.screen.nav.destination.AppIconDestination
import com.xah.transition.ui.screen.nav.destination.BezierSettingsDestination
import com.xah.transition.ui.screen.nav.destination.CornerSettingsDestination
import com.xah.transition.ui.screen.nav.destination.HomeDestination
import com.xah.transition.ui.screen.nav.destination.SecondDestination
import com.xah.transition.ui.screen.nav.destination.ThirdDestination
import com.xah.transition.ui.screen.test.CubicBezierEditor
import com.xah.transition.ui.screen.nav.window.BottomDialogWindow
import com.xah.transition.ui.screen.nav.window.BottomSheetWindow
import com.xah.transition.ui.screen.nav.window.CenterDialogWindow
import com.xah.transition.ui.screen.nav.window.DialogFloatingWindow
import com.xah.transition.ui.style.topBarTransplantColor
import com.xah.transition.ui.util.PermissionSet.checkAndRequestStoragePermission
import com.xah.transition.ui.util.UiHolder
import com.xah.transition.util.Starter
import kotlin.math.roundToInt

private fun Modifier.blur(enableBlur : Boolean,radius : Dp) : Modifier {
    return if(enableBlur) {
        this.blur(radius)
    } else {
        this
    }
}

private fun Modifier.scale(
    scale: Float
) : Modifier {
    return this.graphicsLayer {
        scaleX = scale
        scaleY = scale
    }
}

private fun Modifier.backgroundEffect(
    enableBlur : Boolean,
    blurRadius : Dp,
    scale: Float
) : Modifier {
    return this
        .blur(enableBlur, blurRadius)
        .scale(scale)
}


@Composable
fun App(
    firstPage : Destination? = null
) {
    var arg1 by remember { mutableStateOf(1) }
    val dependencies = rememberNavDependencies(arg1) {
        put(arg1, tag = "args1")
        put("1", tag = "args2")
    }
    val navigationController = rememberNavController(firstPage ?: HomeDestination)
    val inHomeDest = navigationController.current.destination == navigationController.startDestination || navigationController.transitionEntry?.to?.destination == navigationController.startDestination || navigationController.transitionEntry?.from?.destination == navigationController.startDestination
    val displayWallpaper = UiHolder.imageBitmap != null
    Box(modifier = Modifier.fillMaxSize()) {
       if(UiHolder.enableWallpaper && displayWallpaper && inHomeDest) {
           val progress = navigationController.transitionProgress.value
           val blurRadius = when(navigationController.transitionLevel) {
               EffectLevel.FULL -> lerp(navigationController.defaultTransitionEffect.pageEffect.backgroundEffect.effect.blur.start,navigationController.defaultTransitionEffect.pageEffect.backgroundEffect.effect.blur.end,progress)
               else -> navigationController.defaultTransitionEffect.pageEffect.backgroundEffect.effect.blur.start
           }
           val scale = when(navigationController.transitionLevel) {
               EffectLevel.NO_BLUR -> lerp(navigationController.defaultTransitionEffect.pageEffect.backgroundEffect.effect.scale.start,2 - navigationController.defaultTransitionEffect.pageEffect.backgroundEffect.effect.scale.end,progress)
               EffectLevel.FULL -> lerp(navigationController.defaultTransitionEffect.pageEffect.backgroundEffect.effect.scale.start,2 - navigationController.defaultTransitionEffect.pageEffect.backgroundEffect.effect.scale.end,progress)
               EffectLevel.NO_SCALE -> navigationController.defaultTransitionEffect.pageEffect.backgroundEffect.effect.scale.start
               EffectLevel.NONE -> navigationController.defaultTransitionEffect.pageEffect.backgroundEffect.effect.scale.start
           }

           Image(
                bitmap = UiHolder.imageBitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .backgroundEffect(navigationController.enableBlur, blurRadius, scale),
                contentScale = ContentScale.Crop
            )
       }
        SharedNavHost(
            navController = navigationController,
            modifier = Modifier.let {
                if(UiHolder.enableWallpaper && displayWallpaper) {
                    it
                } else {
                    it.background(MaterialTheme.colorScheme.surface)
                }
            },
            dependencies = dependencies,
        )
    }
}

private val appList = listOf(
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
    val floatingController = LocalFloatingController.current
    val pickMediaLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { imageUri ->
            // 使用 context 读取图片为 ImageBitmap
            UiHolder.imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
        }
    }

    val levelList = remember { EffectLevel.entries }
    val filedList = remember {
        listOf(
            Pair(null,"不强制"),
            Pair(ContainerFilledStrategy.Clip,"裁切"),
            Pair(ContainerFilledStrategy.Stretch,"拉伸"),
            Pair(ContainerFilledStrategy.Color(Color.Black),"色彩"),
        )
    }
    val activity = LocalActivity.current

    val displayWallpaper = UiHolder.imageBitmap != null
    val d = !UiHolder.enableWallpaper && displayWallpaper

    Box(modifier = Modifier.fillMaxSize()) {
        if(d) {
            Image(
                bitmap = UiHolder.imageBitmap!!.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }
        Scaffold(
            containerColor = if(!d) MaterialTheme.colorScheme.surface else Color.Transparent,
            floatingActionButton = {
                SharedContainer (
                    containerColor = MaterialTheme.colorScheme.inversePrimary,
                    key = CornerSettingsDestination.key,
                    shape = FloatingActionButtonDefaults.shape as CornerBasedShape
                ) {
                    FloatingActionButton(
                        elevation = FloatingActionButtonDefaults.elevation(0.dp,0.dp,0.dp,0.dp),
                        containerColor = MaterialTheme.colorScheme.inversePrimary,
                        shape = NoneRoundShape,
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
                            modifier = Modifier.padding(CARD_NORMAL_DP*2)
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
                        val destination = SecondDestination(userId = index,false)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            SharedContainer (
                                key = destination.key,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small,
                            ) {
                                Card(
                                    shape = NoneRoundShape,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    TransplantListItem(
                                        headlineContent = { Text("Screen #${index}") },
                                        modifier = Modifier.clickable {
                                            navController.push(destination)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    item {
                        val dest = SecondDestination(888,false)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            Card(
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                TransplantListItem(
                                    headlineContent = { Text("灵动岛动效") },
                                    modifier = Modifier.clickable {
                                        navController.push(dest, effect = IslandTransitionEffect())
                                    }
                                )
                            }
                        }
                    }
                    item {
                        val dest = SecondDestination(888,false)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            Card(
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                TransplantListItem(
                                    headlineContent = { Text("侧方岛动效") },
                                    modifier = Modifier.clickable {
                                        navController.push(dest, effect = IslandTransitionEffect(position = TransformOrigin(1f,0.5f)))
                                    }
                                )
                            }
                        }
                    }
                    item {
                        val dest = SecondDestination(888,false)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            Card(
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                TransplantListItem(
                                    headlineContent = { Text("对角岛动效") },
                                    modifier = Modifier.clickable {
                                        navController.push(dest, effect = IslandTransitionEffect(position = TransformOrigin(1f,0f), rotation = Rotation(z = 30f)))
                                    }
                                )
                            }
                        }
                    }
                    item {
                        val dest = SecondDestination(888,false)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            Card(
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                TransplantListItem(
                                    headlineContent = { Text("推入动效") },
                                    modifier = Modifier.clickable {
                                        navController.push(dest, effect = SlideTransitionEffect())
                                    }
                                )
                            }
                        }
                    }
                    item {
                        val dest = SecondDestination(888,false)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            Card(
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                TransplantListItem(
                                    overlineContent = { Text("屏幕内") },
                                    headlineContent = { Text("缩放动效") },
                                    modifier = Modifier.clickable {
                                        navController.push(dest, effect = ScaleTransitionEffect(true,false))
                                    }
                                )
                            }
                        }
                    }
                    item {
                        val dest = SecondDestination(888,false)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            Card(
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                TransplantListItem(
                                    overlineContent = { Text("屏幕内 弱化版") },
                                    headlineContent = { Text("缩放动效") },
                                    modifier = Modifier.clickable {
                                        navController.push(dest, effect = ScaleTransitionEffect(false,false))
                                    }
                                )
                            }
                        }
                    }
                    item {
                        val dest = SecondDestination(888,false)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            Card(
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                TransplantListItem(
                                    overlineContent = { Text("屏幕外") },
                                    headlineContent = { Text("缩放动效(屏幕外)") },
                                    modifier = Modifier.clickable {
                                        navController.push(dest, effect = ScaleTransitionEffect(true,true))
                                    }
                                )
                            }
                        }
                    }
                    item {
                        val dest = SecondDestination(888,false)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            Card(
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                TransplantListItem(
                                    overlineContent = { Text("屏幕外 弱化版") },
                                    headlineContent = { Text("缩放动效") },
                                    modifier = Modifier.clickable {
                                        navController.push(dest, effect = ScaleTransitionEffect(false,true))
                                    }
                                )
                            }
                        }
                    }
                    item {
                        val dest = SecondDestination(888,false)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            Card(
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                TransplantListItem(
                                    headlineContent = { Text("透明度动效") },
                                    modifier = Modifier.clickable {
                                        navController.push(dest, effect = FadeTransitionEffect())
                                    }
                                )
                            }
                        }
                    }
                    item {
                        val dest = SecondDestination(888,false)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            Card(
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                TransplantListItem(
                                    headlineContent = { Text("推入动效(左侧)") },
                                    modifier = Modifier.clickable {
                                        navController.push(dest, effect = SlideTransitionEffect(Direction.START))
                                    }
                                )
                            }
                        }
                    }

                    item {
                        LaunchedEffect(activity) {
                            activity?.let { checkAndRequestStoragePermission(it) }
                        }
                        val dest = SecondDestination(888,false)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            Card(
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                TransplantListItem(
                                    headlineContent = { Text("跳转动效") },
                                    modifier = Modifier.clickable {
                                        navController.push(
                                            dest,
                                            effect = JumpTransitionEffect(context)
                                        )
                                    }
                                )
                            }
                        }
                    }
                    item {
                        LaunchedEffect(activity) {
                            activity?.let { checkAndRequestStoragePermission(it) }
                        }
                        val dest = SecondDestination(888,false)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            Card(
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                TransplantListItem(
                                    headlineContent = { Text("跳转动效2") },
                                    modifier = Modifier.clickable {
                                        navController.push(
                                            dest,
                                            effect = JumpTransitionEffect(context,true)
                                        )
                                    }
                                )
                            }
                        }
                    }
                    item {
                        val dest = SecondDestination(888,false)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            Card(
                                shape = MaterialTheme.shapes.small,
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                TransplantListItem(
                                    headlineContent = { Text("翻页动效") },
                                    modifier = Modifier.clickable {
                                        navController.push(dest, effect = FlipTransitionEffect())
                                    }
                                )
                            }
                        }
                    }
                    items(30) { index ->
                        val window = DialogFloatingWindow(index)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            SharedContainer (
                                key = window.key,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small,
                            ) {
                                Card(
                                    shape = NoneRoundShape,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    TransplantListItem(
                                        headlineContent = { Text(window.key ?: "Empty") },
                                        modifier = Modifier.clickable {
                                            floatingController.push(window)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    item {
                        val window = BottomDialogWindow
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            SharedContainer (
                                key = window.key,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small,
                            ) {
                                Card(
                                    shape = NoneRoundShape,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    TransplantListItem(
                                        headlineContent = { Text(window.key ?: "底部Dialog") },
                                        modifier = Modifier.clickable {
                                            floatingController.push(window)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    item {
                        val window = BottomSheetWindow
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            SharedContainer (
                                key = window.key,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small,
                            ) {
                                Card(
                                    shape = NoneRoundShape,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    TransplantListItem(
                                        headlineContent = { Text(window.key ?: "底部Sheet") },
                                        modifier = Modifier.clickable {
                                            floatingController.push(window)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    item {
                        val window = CenterDialogWindow
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            SharedContainer (
                                key = window.key,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small,
                            ) {
                                Card(
                                    shape = NoneRoundShape,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    TransplantListItem(
                                        headlineContent = { Text(window.key ?: "中心弹窗") },
                                        modifier = Modifier.clickable {
                                            floatingController.push(window)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    item {
                        val window = DialogFloatingWindow(888)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            SharedContainer (
                                key = window.key,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small,
                            ) {
                                Card(
                                    shape = NoneRoundShape,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    TransplantListItem(
                                        headlineContent = { Text("容器共享弹窗") },
                                        modifier = Modifier.clickable {
                                            floatingController.push(window)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    items(15,span = { GridItemSpan(maxLineSpan) }) { index ->
                        val destination = SecondDestination(userId = index,true)
                        Box(modifier = Modifier.padding(CARD_NORMAL_DP*2)) {
                            SharedContainer (
                                key = destination.key,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                shape = MaterialTheme.shapes.small,
                            ) {
                                Card(
                                    shape = NoneRoundShape,
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                                ) {
                                    TransplantListItem(
                                        headlineContent = { Text("Screen #${index}") },
                                        modifier = Modifier.clickable {
                                            navController.push(destination)
                                        }
                                    )
                                }
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
                                    Text("容器共享")
                                },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.ic_texture),null)
                                },
                                trailingContent = {
                                    Switch(registry.enabled, onCheckedChange = { registry.enabled = it })
                                },
                                modifier = Modifier.clickable {
                                    registry.enabled = !registry.enabled
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
                                    Icon(painterResource(R.drawable.ic_texture),null)
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
                        Surface(
                            color = cardNormalColor(),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(CARD_NORMAL_DP*2)
                        ) {
                            TransplantListItem(
                                headlineContent = {
                                    Text("倾斜效果")
                                },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.ic_texture),null)
                                },
                                trailingContent = {
                                    Switch(registry.enableTilt, onCheckedChange = { registry.enableTilt = it })
                                },
                                modifier = Modifier.clickable {
                                    registry.enableTilt = !registry.enableTilt
                                },
                            )
                        }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        LaunchedEffect(UiHolder.enablePredictiveBack) {
                            navController.enablePredictiveBack = UiHolder.enablePredictiveBack
                            registry.enablePredictiveBack = UiHolder.enablePredictiveBack
                        }
                        Surface(
                            color = cardNormalColor(),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(CARD_NORMAL_DP*2)
                        ) {
                            TransplantListItem(
                                headlineContent = {
                                    Text("预测式返回")
                                },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.ic_texture),null)
                                },
                                trailingContent = {
                                    Switch(UiHolder.enablePredictiveBack, onCheckedChange = { UiHolder.enablePredictiveBack = it })
                                },
                                modifier = Modifier.clickable {
                                    UiHolder.enablePredictiveBack = !UiHolder.enablePredictiveBack
                                },
                            )
                        }
                    }
                    /*
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Surface(
                            color = cardNormalColor(),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(CARD_NORMAL_DP*2)
                        ) {
                            TransplantListItem(
                                headlineContent = {
                                    Text("预测式返回背景固定")
                                },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.ic_texture),null)
                                },
                                trailingContent = {
                                    Switch(!navController.enablePredictiveBackBackgroundFollow, onCheckedChange = { navController.enablePredictiveBackBackgroundFollow = !navController.enablePredictiveBackBackgroundFollow })
                                },
                                modifier = Modifier.clickable {
                                    navController.enablePredictiveBackBackgroundFollow = !navController.enablePredictiveBackBackgroundFollow
                                },
                            )
                        }
                    }
                     */
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        LaunchedEffect(UiHolder.enablePredictiveBack) {
                            navController.enablePredictiveBack = UiHolder.enablePredictiveBack
                            registry.enablePredictiveBack = UiHolder.enablePredictiveBack
                        }
                        Surface(
                            color = cardNormalColor(),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(CARD_NORMAL_DP*2)
                        ) {
                            TransplantListItem(
                                headlineContent = {
                                    Text("Splash Screen")
                                },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.ic_texture),null)
                                },
                                trailingContent = {
                                    Switch(navController.enableSplashScreen, onCheckedChange = { navController.enableSplashScreen = it })
                                },
                                modifier = Modifier.clickable {
                                    navController.enableSplashScreen = !navController.enableSplashScreen
                                },
                            )
                        }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        LaunchedEffect(UiHolder.enablePredictiveBack) {
                            navController.enablePredictiveBack = UiHolder.enablePredictiveBack
                            registry.enablePredictiveBack = UiHolder.enablePredictiveBack
                        }
                        Surface(
                            color = cardNormalColor(),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(CARD_NORMAL_DP*2)
                        ) {
                            TransplantListItem(
                                headlineContent = {
                                    Text("壁纸缩放")
                                },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.ic_texture),null)
                                },
                                trailingContent = {
                                    Switch(enabled = displayWallpaper, checked = UiHolder.enableWallpaper, onCheckedChange = { UiHolder.enableWallpaper = it })
                                },
                                modifier = Modifier.clickable(displayWallpaper) {
                                    UiHolder.enableWallpaper = !UiHolder.enableWallpaper
                                },
                            )
                        }
                    }
                    items(filedList.size, key = { filedList[it].hashCode() }) { index ->
                        val item = filedList[index]
                        val selected = registry.enforceContainerFilledStrategy == item.first

                        val color = if(selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer

                        Surface(
                            color = color,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(CARD_NORMAL_DP*2)
                        ) {
                            TransplantListItem(
                                headlineContent = {
                                    Text(item.second, color = contentColorFor(color))
                                },
                                overlineContent = {
                                    Text("容器填充方案",color = contentColorFor(color))
                                },
                                modifier = Modifier.clickable {
                                    registry.enforceContainerFilledStrategy = item.first
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
                            var value by remember { mutableStateOf(registry.animationTime.toFloat()) }
                            Column {
                                TransplantListItem(
                                    headlineContent = {
                                        Text("容器共享动画速率 ${value.roundToInt()}ms")
                                    },
                                )
                                CustomSlider(
                                    value = value,
                                    onValueChange = {
                                        value = it
                                        registry.animationTime = value.roundToInt()
                                    },
                                    modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP),
                                    valueRange = 200f..1000f,
                                    steps = 31,
                                    showProcessText = true,
                                    processText = value.roundToInt().toString()
                                )
                            }
                        }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Surface(
                            color = cardNormalColor(),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(CARD_NORMAL_DP*2)
                        ) {
                            var value by remember { mutableStateOf(registry.tiltMaxValue) }
                            Column {
                                TransplantListItem(
                                    headlineContent = {
                                        Text("容器共享倾斜程度 ${value.roundToInt()}")
                                    },
                                )
                                CustomSlider(
                                    value = value,
                                    onValueChange = {
                                        value = it
                                        registry.tiltMaxValue = value
                                    },
                                    modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP),
                                    valueRange = 0f..75f,
                                    steps = 74,
                                    showProcessText = true,
                                    processText = value.roundToInt().toString()
                                )
                            }
                        }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Surface(
                            color = cardNormalColor(),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(CARD_NORMAL_DP*2)
                        ) {
                            var value by remember { mutableStateOf(registry.speedUpRadio) }
                            Column {
                                TransplantListItem(
                                    headlineContent = {
                                        Text("容器共享渐变速率比 $value")
                                    },
                                )
                                CustomSlider(
                                    value = value,
                                    onValueChange = {
                                        value = it
                                        registry.speedUpRadio = value
                                    },
                                    modifier = Modifier.padding(bottom = APP_HORIZONTAL_DP),
                                    valueRange = 1f..10f,
                                    steps = 35,
                                    showProcessText = true,
                                    processText = value.toString()
                                )
                            }
                        }
                    }
                    /*
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Surface(
                            color = cardNormalColor(),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(CARD_NORMAL_DP*2)
                        ) {
                            TransplantListItem(
                                headlineContent = {
                                    Text("着色器")
                                },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.ic_texture),null)
                                },
                                trailingContent = {
                                    Switch(checked = navController.enableShader, onCheckedChange = { navController.enableShader = it })
                                },
                                modifier = Modifier.clickable {
                                    navController.enableShader = !navController.enableShader
                                },
                            )
                        }
                    }
                     */
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Surface(
                            color = cardNormalColor(),
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.padding(CARD_NORMAL_DP*2)
                        ) {
                            TransplantListItem(
                                headlineContent = {
                                    Text("Github")
                                },
                                leadingContent = {
                                    Icon(painterResource(R.drawable.ic_github),null)
                                },
                                modifier = Modifier.clickable {
                                    Starter.startWebUrl(context,"https://github.com/Chiu-xaH/SharedNav")
                                },
                            )
                        }
                    }
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Spacer(Modifier
                            .navigationBarsPadding()
                            .height((APP_HORIZONTAL_DP + innerPadding.calculateBottomPadding()) * 3))
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
                            shape = NoneRoundShape,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondScreen() {
    val navController = LocalNavController.current
    Scaffold(
        topBar = {
            MediumTopAppBar(
                colors = topBarTransplantColor(),
                title = { Text("二级界面") },
                navigationIcon = {
                    TopBarNavigationIcon()
                }
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            LazyColumn {
                item { Spacer(Modifier.height(innerPadding.calculateTopPadding())) }
                items(30) {
                    DividerTextExpandedWithShared("副标题$it") {
                        CustomCard(
                            color = cardNormalColor(),
                            modifier = Modifier.clickable {
                                navController.push(ThirdDestination)
                            }
                        ) {
                            repeat(3) { r ->
                                TransplantListItem(
                                    headlineContent = {
                                        Text("Push to Third $r")
                                    },
                                )
                            }
                        }
                    }
                }
                item { Spacer(Modifier.height(innerPadding.calculateBottomPadding())) }
            }
        }
    }
}

@Composable
fun AppIconScreen(app : AppIconBean) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        val navController = LocalNavController.current
        val scope = rememberCoroutineScope()

        Text("${app.name}", modifier = Modifier.align(Alignment.Center))

//        Box(
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .navigationBarsPadding()
//                .padding(APP_HORIZONTAL_DP)
//                .size(150.dp,CARD_NORMAL_DP*2)
//                .background(MaterialTheme.colorScheme.onSurface, MaterialTheme.shapes.extraSmall)
//                .pointerInput(Unit) {
//                    awaitPointerEventScope {
//
//                        var totalOffset = Offset.Zero
//                        var isDragging = false
//
//                        while (true) {
//                            var state : SharedContainerState? = null
//                            val event = awaitPointerEvent()
//                            val pan = event.calculatePan()
//                            val anyPressed = event.changes.any { it.pressed }
//
//                            if (anyPressed) {
//
//                                // 第一次按下
//                                if (!isDragging) {
//                                    isDragging = true
//                                    totalOffset = Offset.Zero
//
//                                    scope.launch {
//                                        state = navController.startPredictiveBackShared()
//                                    }
//                                }
//
//                                // 累计位移（只取Y，模拟上滑返回）
//                                totalOffset += Offset(0f, pan.y)
//
//                                // 限制只能“向上滑”
//                                if (totalOffset.y > 0f) {
//                                    totalOffset = Offset(0f, 0f)
//                                }
//
//                                // 计算进度（你可以调这个值）
//                                val progress = (-totalOffset.y / 600f)
//                                    .coerceIn(0f, 1f)
//
//                                // 更新动画
//                                navController.updatePredictiveBackShared(
//                                    progress,
//                                    totalOffset,
//                                    state
//                                )
//
//                            } else if (isDragging) {
//
//                                // 松手
//                                val progress = (-totalOffset.y / 600f)
//
//                                if (progress > 0.3f) {
//                                    // 触发返回
//                                    navController.confirmPredictiveBackShared(state)
//                                } else {
//                                    // 取消
//                                    navController.cancelPredictiveBackShared(state)
//                                }
//
//                                state = null
//                                isDragging = false
//                                totalOffset = Offset.Zero
//                            }
//                        }
//                    }
//                }
//        )
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
                navigationIcon = {
                    TopBarNavigationIcon()
                }
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
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(corner.dp))
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
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = APP_HORIZONTAL_DP)) {
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
                navigationIcon = {
                    TopBarNavigationIcon()
                }
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
                .verticalScroll(rememberScrollState())
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


