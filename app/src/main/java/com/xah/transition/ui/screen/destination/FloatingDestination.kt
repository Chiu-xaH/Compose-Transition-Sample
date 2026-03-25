package com.xah.transition.ui.screen.destination

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xah.container.component.base.SharedContainer
import com.xah.floating.util.LocalFloatingController
import com.xah.transition.ui.screen.test.ConfirmFloating
import com.xah.transition.ui.screen.test.InfoFloating
import com.xah.transition.ui.util.NavDestination

object FloatingDestination : NavDestination() {
    override val title: String = "浮窗"
    override val key = "floating"

    @Composable
    override fun Content() {
        val controller = LocalFloatingController.current

        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                val dest2 = ConfirmFloating("确认执行操作吗？") {
                    // 执行业务逻辑
                }
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentSize provides 0.dp
                ) {
                    SharedContainer(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        key = dest2.key,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Button (
                            shape = RoundedCornerShape(0.dp),
                            onClick = {
                                controller.push(dest2)
                            }
                        ) {
                            Text("弹出确认浮窗")
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))

                val dest = InfoFloating("这是一个信息浮窗")
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentSize provides 0.dp
                ) {
                    SharedContainer(
                        containerColor = MaterialTheme.colorScheme.surface,
                        key = dest.key,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Button (
                            shape = RoundedCornerShape(0.dp),
                            onClick = {
                                controller.push(dest)
                            }
                        ) {
                            Text("弹出浮窗")
                        }
                    }
                }
            }
        }
    }
}