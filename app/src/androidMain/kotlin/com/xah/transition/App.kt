package com.xah.transition

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.xah.navigation.model.dest.DeepLink
import com.xah.navigation.registry.DeepLinkRegistry
import com.xah.transition.ui.screen.nav.destination.BezierSettingsDestination
import com.xah.transition.ui.screen.nav.destination.SecondDestination

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext

        DeepLinkRegistry.init(listOf(
            SecondDeepLink,BezierSettingsDeepLink
        ))
    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        private val SecondDeepLink by lazy {
            DeepLink(SecondDestination.KEY) { uri ->
                val userId = uri.getQueryParameter("id")?.toIntOrNull() ?: return@DeepLink null
                SecondDestination(userId = userId, isLong = true)
            }
        }
        private val BezierSettingsDeepLink by lazy {
            DeepLink(BezierSettingsDestination.key) { BezierSettingsDestination }
        }
    }
}
