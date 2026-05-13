package com.xah.transition.ui.screen.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.xah.container.component.base.SharedContainer
import com.xah.container.component.base.SharedContent
import com.xah.container.model.ContainerFilledStrategy
import com.xah.container.model.ContentStrategy
import com.xah.floating.anim.DefaultForegroundEffect
import com.xah.floating.model.Window
import com.xah.floating.model.anim.ForegroundEffect
import com.xah.floating.model.componment.SharedWindow
import com.xah.transition.R
import com.xah.transition.ui.component.APP_HORIZONTAL_DP

data class DialogFloatingWindow(val index : Int) : SharedWindow() {
    override val key = "Dialog #$index"
    override val modifier = Modifier.padding(horizontal = APP_HORIZONTAL_DP)

    @Composable
    override fun BoxScope.Content() {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shape = shape,
        ) {
            Box(modifier = Modifier.height(height = 200.dp)) {
//                SharedContent (
//                    key = "element",
//                    contentStrategy = ContentStrategy.Layer(isFloating = true),
//                    shape = NoneRoundShape
//                    modifier = Modifier.align(Alignment.TopEnd).padding(APP_HORIZONTAL_DP)
//                ) {
//                    Icon(painterResource(R.drawable.ic_settings),null, tint = Color.Blue)
//                }
                Text(key, modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

