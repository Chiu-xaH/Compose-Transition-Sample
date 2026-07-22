package com.xah.transition.ui.screen.nav.destination

import androidx.compose.runtime.Composable
import com.xah.transition.ui.screen.SecondScreen
import com.xah.shader.state.ShaderState
import com.xah.transition.ui.util.NavDestination

data class SecondDestination(
    val userId: Int,
    val isLong : Boolean,
    val shaderState: ShaderState? = null
) : NavDestination() {

    override val title: String = "二级界面"
    override val key = "KEY_${userId}_$isLong"

    companion object {
        const val KEY = "second"
    }

    @Composable
    override fun Content() {
        SecondScreen(shaderState,userId)
    }
}