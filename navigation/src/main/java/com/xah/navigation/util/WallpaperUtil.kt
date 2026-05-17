package com.xah.navigation.util

import android.Manifest
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import androidx.annotation.RequiresPermission
import com.sharednav.common.util.LogUtil

/**
 * 需存储权限
 */
@RequiresPermission(anyOf = ["android.permission.READ_WALLPAPER_INTERNAL", Manifest.permission.MANAGE_EXTERNAL_STORAGE])
fun getWallpaper(context: Context): Bitmap? {
    try {
        val manager = WallpaperManager.getInstance(context)
        return (manager.drawable as? BitmapDrawable)?.bitmap
    } catch (e : Exception) {
        LogUtil.error(e)
        return null
    }
}