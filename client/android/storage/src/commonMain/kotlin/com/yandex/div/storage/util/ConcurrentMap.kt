package com.yandex.div.storage.util

internal expect fun <K : Any, V : Any> concurrentMap(): MutableMap<K, V>
