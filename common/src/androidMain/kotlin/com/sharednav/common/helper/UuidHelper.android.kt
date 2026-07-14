package com.sharednav.common.helper

import java.util.UUID

actual fun randomUUID(): String = UUID.randomUUID().toString()
