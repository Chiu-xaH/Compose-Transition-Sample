package com.xah.floating.model

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.xah.container.component.base.SharedContent
import com.xah.container.model.ContentStrategy
import com.xah.floating.anim.DefaultForegroundEffect
import com.xah.floating.model.anim.ForegroundEffect

abstract class Window {

    abstract val key : String?
    open val animation : ForegroundEffect = DefaultForegroundEffect

    open fun onDismissed() {}

    @Composable
    abstract fun BoxScope.Content()

    @Composable
    open fun Layer() {
        Box(modifier = Modifier.fillMaxSize()) {
            Content()
        }
    }
}
