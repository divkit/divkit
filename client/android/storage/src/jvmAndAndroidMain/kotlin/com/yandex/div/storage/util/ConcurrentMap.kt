package com.yandex.div.storage.util

import java.util.concurrent.ConcurrentHashMap

internal actual fun <K : Any, V : Any> concurrentMap(): MutableMap<K, V> = ConcurrentHashMap()
