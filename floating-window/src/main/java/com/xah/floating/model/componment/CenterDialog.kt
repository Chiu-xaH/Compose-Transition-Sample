package com.xah.floating.model.componment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xah.floating.model.Window

abstract class CenterDialog : Window() {
    override val key: String? = null

    open val modifier : Modifier = Modifier

    @Composable
    override fun Layer() {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = modifier.then(
                    Modifier
                        .align(Alignment.Center)
                        .navigationBarsPadding()
                        .statusBarsPadding()
                )
            ) {
                Content()
            }
        }
    }
}