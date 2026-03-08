package com.xah.transition.ui.uitls

import com.xah.navigation.model.dest.SharedDestination

abstract class NavDestination : SharedDestination() {
    abstract val title : String
    open val icon : Int? = null
}