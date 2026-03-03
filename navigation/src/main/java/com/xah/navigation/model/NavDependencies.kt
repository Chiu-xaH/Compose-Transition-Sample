package com.xah.navigation.model

import kotlin.reflect.KClass


class NavDependencies {
    val map = mutableMapOf<Pair<KClass<*>, String?>, Any>()

    inline fun <reified T : Any> put(value: T, tag: String? = null) {
        map[T::class to tag] = value
    }

    inline fun <reified T : Any> get(tag: String? = null): T {
        return map[T::class to tag] as T
    }
}