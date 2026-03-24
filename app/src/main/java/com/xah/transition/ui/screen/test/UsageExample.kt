package com.xah.transition.ui.screen.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sharednav.common.util.LogUtil
import com.xah.floating.model.FloatingWindow
import com.xah.floating.component.FloatingRoot
import com.xah.floating.util.LocalFloatingController

class ConfirmFloating(
    private val message: String,
    private val onConfirm: () -> Unit,
) : FloatingWindow() {

    override fun onDismissed() {
        LogUtil.debug("${super.hashCode()}")
    }

    @Composable
    override fun Content() {
        val controller = LocalFloatingController.current

        Box(modifier = Modifier.fillMaxSize()) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                tonalElevation = 6.dp,
                modifier = Modifier
                    .width(280.dp)
                    .padding(horizontal = 16.dp)
                    .align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = {
                        onConfirm()
                        controller.pop()
                    }) {
                        Text("确认")
                    }
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { controller.pop() }) {
                        Text("取消")
                    }
                }
            }
        }
    }
}

class InfoFloating(private val title: String) : FloatingWindow() {
    @Composable
    override fun Content() {
        val controller = LocalFloatingController.current
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(260.dp)
                .padding(horizontal = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(16.dp))
                Button(onClick = {
                    // 在 info 浮窗上再弹一个确认浮窗，演示多层叠加
                    controller.push(
                        ConfirmFloating("是否关闭所有浮窗？") {
                        }
                    )
                }) {
                    Text("再弹一个浮窗")
                }
                Spacer(Modifier.height(8.dp))
                Button(onClick = { controller.pop() }) {
                    Text("关闭")
                }
            }
        }
    }
}

@Composable
@Preview
private fun SampleContent() {
    FloatingRoot {
        val controller = LocalFloatingController.current

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {

                Button(onClick = {
                    controller.push(
                        ConfirmFloating("确认执行操作吗？") {
                            // 执行业务逻辑
                        }
                    )
                }) {
                    Text("弹出确认浮窗")
                }
                Spacer(Modifier.height(12.dp))

                Button(onClick = {
                    controller.push(InfoFloating("这是一个信息浮窗"))
                }) {
                    Text("弹出浮窗")
                }
            }
        }
    }
}