package com.xah.transition.ui.component

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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.Dialog
import com.xah.navigation.model.action.LaunchMode
import com.xah.navigation.util.LocalNavController
import com.xah.transition.R
import com.xah.transition.ui.util.NavDestination
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarNavigationIcon() {
    val navController = LocalNavController.current
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
                ){
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
                                                navController.push(item.destination, LaunchMode.PopToExisting)
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
        modifier = Modifier
            .padding(horizontal = CARD_NORMAL_DP/2)
            .clip(CircleShape)
            .combinedClickable(
                enabled = enabled,
                onClick = {
                    navController.pop()
                },
                // TODO 预留唤出启动台
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
                painterResource(R.drawable.ic_arrow_back),
                contentDescription = null,
                tint = if(enabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(.5f)
                }
            )
        }
    }
}