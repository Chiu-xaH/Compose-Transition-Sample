package com.xah.floating.model.componment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.xah.container.component.base.SharedContent
import com.xah.container.model.ContentStrategy
import com.xah.floating.model.Window

abstract class SharedWindow : Window() {

    open val align : Alignment = Alignment.Center
    open val shape : CornerBasedShape @Composable get() = MaterialTheme.shapes.large
    open val modifier : Modifier = Modifier

    @Composable
    override fun Layer() {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier.align(align)
            ) {
                if(key == null) {
                    Box(modifier = modifier.clip(shape)) {
                        Content()
                    }
                } else {
                    SharedContent(
                        key = key!!,
                        shape = shape,
                        contentStrategy = ContentStrategy.Layer(true),
                        modifier = modifier,
                        content = { Content() }
                    )
                }
            }
        }
    }
}
