package com.xah.transition.util

expect object PermissionSet {
    @JvmStatic
    fun checkAndRequestStoragePermission(activity: PlatformActivity)
}