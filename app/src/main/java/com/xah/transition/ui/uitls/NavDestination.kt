package com.xah.transition.ui.uitls

import com.xah.navigation.model.Destination

abstract class NavDestination : Destination() {
    abstract val title : String
    open val icon : Int? = null
}