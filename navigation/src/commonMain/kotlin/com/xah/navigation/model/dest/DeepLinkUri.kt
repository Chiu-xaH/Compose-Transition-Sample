package com.xah.navigation.model.dest

data class DeepLinkUri(
    val scheme: String?,
    val host: String?,
    val path: String?,
    private val queryParameters: Map<String, List<String>>
) {
    operator fun get(key: String): String? = queryParameters[key]?.firstOrNull()

    fun getQueryParameter(key: String): String? = queryParameters[key]?.firstOrNull()

    fun getQueryParameters(key: String): List<String> = queryParameters[key].orEmpty()

    fun hasQueryParameter(key: String): Boolean = queryParameters.containsKey(key)
}