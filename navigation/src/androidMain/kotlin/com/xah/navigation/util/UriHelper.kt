package com.xah.navigation.util

import android.net.Uri
import com.xah.navigation.model.dest.DeepLinkUri

fun Uri.toDeepLinkUri(): DeepLinkUri {
    val parameters = mutableMapOf<String, List<String>>()

    for (key in queryParameterNames) {
        parameters[key] = getQueryParameters(key)
    }

    return DeepLinkUri(
        scheme = scheme,
        host = host,
        path = path,
        queryParameters = parameters
    )
}