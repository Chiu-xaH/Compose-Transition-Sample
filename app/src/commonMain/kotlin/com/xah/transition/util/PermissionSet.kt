package com.xah.transition.util

expect object PermissionSet {
    fun checkAndRequestStoragePermission(activity: PlatformActivity)
}