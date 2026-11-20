package com.yandex.div.storage.templates

import com.yandex.div.core.annotations.InternalApi

/**
 * Reads missing templates by hash. Returns `null` when the cache already covers the request.
 */
@InternalApi
public inline fun <R : Any> loadMissingTemplates(
    templateHashes: Set<String>,
    cachedHashes: Set<String>,
    readTemplates: (Set<String>) -> R,
): R? {
    val missingHashes = templateHashes.toMutableSet().apply { removeAll(cachedHashes) }
    return if (missingHashes.isEmpty()) null else readTemplates(missingHashes)
}
