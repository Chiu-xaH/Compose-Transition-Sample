package com.xah.transition.util

import com.xah.navigation.util.PlatformContext

expect object Starter {
    //传入网页URL打开
    fun startWebUrl(context: PlatformContext,url : String)
}