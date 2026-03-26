package com.xah.transition.ui.screen.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.xah.container.component.base.SharedContent
import com.xah.floating.model.Window
import com.xah.transition.ui.component.APP_HORIZONTAL_DP

data class DialogFloatingWindow(val index : Int) : Window() {
    override val key: String = "Dialog #$index"

    @Composable
    override fun Content() {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.align(Alignment.Center)) {
                SharedContent(
                    key = key,
                    shape = MaterialTheme.shapes.large,
                    isFullScreen = false,
                    modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.surface,
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Box(modifier = Modifier.height(height = 200.dp)) {
                            Text(key, modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
            }
        }
    }
}

