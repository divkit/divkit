package com.yandex.div.webp

import androidx.collection.LruCache

/**
 * A simple cache manager for storing downloaded webp images bytes
 */
internal class WebpCacheManager(maxBytes: Int = 8 * 1024 * 1024) {

    private val cache = object : LruCache<String, ByteArray>(maxBytes) {
        override fun sizeOf(key: String, value: ByteArray) = value.size
    }

    fun get(imageUrl: String) = cache.get(imageUrl)

    fun put(imageUrl: String, data: ByteArray) { cache.put(imageUrl, data) }
}
