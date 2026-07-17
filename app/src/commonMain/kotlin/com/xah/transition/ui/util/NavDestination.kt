package com.xah.transition.ui.util

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xah.navigation.model.dest.Destination
import com.xah.transition.ui.component.StatusIcon
import org.jetbrains.compose.resources.DrawableResource
import sharednav.app.generated.resources.Res
import sharednav.app.generated.resources.ic_texture

abstract class NavDestination : Destination() {
    abstract val title : String
    open val icon : DrawableResource = Res.drawable.ic_texture

    override val PlaceHolder = @Composable {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Box(
                modifier = Modifier.align(Alignment.Center)
            ) {
                StatusIcon(icon, title, textColor = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}