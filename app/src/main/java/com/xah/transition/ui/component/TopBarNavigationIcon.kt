package com.xah.transition.ui.component

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.xah.navigation.model.action.LaunchMode
import com.xah.navigation.util.LocalNavController
import com.xah.transition.R
import com.xah.transition.ui.util.NavDestination
import kotlinx.coroutines.launch

/*
TODO
[UX]手指按住向右拖动调用预测式返回，反向松手取消预测式，继续松手执行返回
[UX]手指按住向下拖动跟手图标，模糊缩放背景，到一定程度显示ControlCenter，未达到阈值时松手返回，达到阈值后松手仍保持打开状态，点击空白区域或Icon关闭面板
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarNavigationIcon(
    modifier : Modifier = Modifier
) {
    val navController = LocalNavController.current
    val activity = LocalActivity.current
    val scope = rememberCoroutineScope()
    val queue = navController.stack.reversed()
    var displayDialog by remember { mutableStateOf(false) }

    if(displayDialog) {
        Dialog(
            onDismissRequest = { displayDialog = false }
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .clickable(
                    // 去掉水波纹
                    interactionSource = null,
                    indication = null
                ) {
                    displayDialog = false
                }
            ) {
                Column(
                    modifier = Modifier
                        .statusBarsPadding()
                        .navigationBarsPadding()
                        .padding(vertical = APP_HORIZONTAL_DP)
                        .align(Alignment.TopCenter)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f, fill = false),
                        shape = MaterialTheme.shapes.medium,
                    ) {
                        LazyColumn {
                            items(queue.size) { index ->
                                val item = queue[index]
                                val dest = item.destination as NavDestination
                                val title = dest.title
                                val isCurrent = index == 0
                                TransplantListItem(
                                    headlineContent = {
                                        Text(title ,fontWeight = if(isCurrent) FontWeight.Bold else FontWeight.Normal)
                                    },
                                    trailingContent = {
                                        if(isCurrent) {
                                            Icon(painterResource(R.drawable.ic_arrow_back),null)
                                        }
                                    },
                                    modifier = Modifier.clickable {
                                        if(isCurrent) {
                                            displayDialog = false
                                        } else {
                                            scope.launch {
                                                navController.push(item.destination, LaunchMode.PopToExisting())
                                                displayDialog = false
                                            }
                                        }
                                    }
                                )
                                if(index != queue.size-1) {
                                    HorizontalDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    val enabled = navController.canPop()

    Box(
        modifier = modifier
            .padding(horizontal = CARD_NORMAL_DP/2)
            .clip(CircleShape)
            .combinedClickable(
                onClick = {
                    if(enabled) {
                        navController.pop()
                    } else {
                        activity?.finish()
                    }
                },
                onDoubleClick = null,
                onLongClick = {
                    displayDialog = true
                }
            )
    ) {
        Box(
            modifier = Modifier.padding(DIVIDER_TEXT_VERTICAL_PADDING)
        ) {
            Icon(
                painterResource(
                    if(enabled) {
                        R.drawable.ic_arrow_back
                    } else {
                        R.drawable.ic_close
                    }
                ),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
