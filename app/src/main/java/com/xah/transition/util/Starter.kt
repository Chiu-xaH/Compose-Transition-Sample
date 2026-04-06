package com.xah.transition.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri
import com.sharednav.common.util.LogUtil
import com.xah.transition.util.ToastUtil.showToast

object Starter {
    //传入网页URL打开
    @JvmStatic
    fun startWebUrl(context: Context,url : String) {
        try {
            val it = Intent(Intent.ACTION_VIEW, url.toUri())
            if (context !is Activity) {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(it)
        } catch (e : Exception) {
            LogUtil.error(e)
            showToast(context,"启动浏览器失败")
        }
    }
}