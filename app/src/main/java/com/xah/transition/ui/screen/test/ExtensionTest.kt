package com.xah.transition.ui.screen.test

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.xah.container.container.pixelExtension
import com.xah.container.model.ExtensionDirection
import com.xah.transition.R

@Composable
@Preview
fun Test() {
    var rect by remember { mutableStateOf<Rect?>(null) }
    val graphicsLayer = rememberGraphicsLayer()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.screenWidthDp > configuration.screenHeightDp

    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.align(Alignment.Center)) {
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
                        drawContent()
                        graphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                    }
            ) {
                Image(painterResource(R.drawable.ic_iqiyi),null)
            }
            Box(
                modifier = Modifier
                    .zIndex(-1f)
                    .pixelExtension(graphicsLayer,rect,ExtensionDirection.VERTICAL)
            )
        }
    }
}