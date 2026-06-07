package com.xah.navigation.model.dest

import android.net.Uri

/**
 * 只允许query的形式传入基本参数 Uri格式：scheme://host?query=xxx
 *
 * 例如：UserDestination(id = 1) 转换为深链接Path：user?id=1，开发者必须重写深链接解析为Dest的函数
 */
class DeepLink<D : Destination>(
    val host: String,
    /**
     * Uri 映射为 Destination
     */
    val parse: (Uri) -> D?
)

/*
abstract class DeepLink<D : Destination>(
    val host: String
) {
    /**
     * Uri 映射为 Destination
     */
    abstract fun parse(uri: Uri): D?
}
 */