package com.xah.transition.ui.uitls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xah.navigation.model.dest.Destination

abstract class NavDestination : Destination() {
    abstract val title : String
    open val icon : Int? = null
    override val PlaceHolder = @Composable {
        Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
            Text("Splash Screen", modifier = Modifier.align(Alignment.Center))
        }
    }
}