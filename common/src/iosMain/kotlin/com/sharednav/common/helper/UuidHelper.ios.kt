package com.sharednav.common.helper

import platform.Foundation.NSUUID

actual fun randomUUID(): String = NSUUID().UUIDString