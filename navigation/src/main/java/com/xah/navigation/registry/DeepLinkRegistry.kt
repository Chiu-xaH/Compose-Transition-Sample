package com.xah.navigation.registry

import android.net.Uri
import com.sharednav.common.util.LogUtil
import com.xah.navigation.model.dest.DeepLink
import com.xah.navigation.model.dest.Destination
import kotlin.collections.get

object DeepLinkRegistry {
    private val parsers = mutableMapOf<String, DeepLink<*>>()

    private fun register(deepLink: DeepLink<*>) {
        LogUtil.debug("register deeplink ${deepLink.host}")
        parsers[deepLink.host] = deepLink
    }

    fun parse(uri: Uri): Destination? {
        // 去掉scheme://和query参数，拿到key
        val path = uri.host
        val deepLink = parsers[path]
        if(deepLink == null) {
            LogUtil.error("Unfound deeplink: $uri")
        } else {
            LogUtil.debug("Found deeplink: $uri")
        }
        val result = deepLink?.parse(uri)
        if(result == null) {
            LogUtil.error("Unparsed deeplink: $uri")
        } else {
            LogUtil.debug("Parsed deeplink: ${result.javaClass.simpleName}")
        }
        return result
    }

    fun init(deepLinks : List<DeepLink<*>>) {
        deepLinks.forEach { register(it) }
    }
}