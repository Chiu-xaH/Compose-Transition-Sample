package com.xah.transition.util

import android.app.Activity
import android.content.Intent
import androidx.core.net.toUri
import com.sharednav.common.util.LogUtil
import com.xah.navigation.util.PlatformContext
import com.xah.transition.util.ToastUtil.showToast

actual object Starter {
    @JvmStatic
    actual fun startWebUrl(context: PlatformContext, url: String)  {
        try {
            val it = Intent(Intent.ACTION_VIEW, url.toUri())
            if (context.context !is Activity) {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.context.startActivity(it)
        } catch (e : Exception) {
            LogUtil.error(e)
            showToast(context.context,"启动浏览器失败")
        }
    }
}