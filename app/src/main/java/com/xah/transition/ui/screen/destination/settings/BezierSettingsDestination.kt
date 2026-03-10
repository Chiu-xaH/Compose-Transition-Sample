package com.xah.transition.ui.screen.destination.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.xah.transition.R
import com.xah.transition.ui.screen.BezierSettingsScreen
import com.xah.transition.ui.uitls.NavDestination

data object BezierSettingsDestination : NavDestination() {

    override val title: String = "动画曲线设置"
    override val key = "settings_bezier"
    override val icon = R.drawable.animation

    @Composable
    override fun Content() {
        BezierSettingsScreen(title)
    }
}