package com.xah.transition.ui.screen.window

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import com.xah.floating.model.componment.BottomDialog
import com.xah.floating.model.componment.BottomSheet
import com.xah.floating.model.componment.CenterDialog
import com.xah.transition.ui.component.APP_HORIZONTAL_DP

object CenterDialogWindow : CenterDialog() {

    override val modifier = Modifier.padding(APP_HORIZONTAL_DP)

    @Composable
    override fun BoxScope.Content() {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shape = MaterialTheme.shapes.large
        ) {
            Box(modifier = Modifier.height(height = 200.dp)) {
                Text("key", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

