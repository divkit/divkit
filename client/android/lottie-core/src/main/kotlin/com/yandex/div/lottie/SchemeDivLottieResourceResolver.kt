package com.yandex.div.lottie

import android.net.Uri
import android.util.LruCache

internal class SchemeDivLottieResourceResolver<T : Any>(
    private val scheme: String,
    private val resourceDescription: String,
    private val mapper: (String) -> T?,
) {
    private val resolvedResources = LruCache<String, T>(RESOLVED_RESOURCE_CACHE_SIZE)

    @Synchronized
    fun canLoad(url: String): Boolean = url.hasScheme(scheme) && resolveMappedResource(url) != null

    @Synchronized
    fun resolve(url: String): Result<T> {
        if (!url.hasScheme(scheme)) {
            return Result.failure(IllegalArgumentException("Expected $scheme URL, got: $url"))
        }
        val resource = resolveMappedResource(url) ?: return Result.failure(
            IllegalArgumentException("Failed to resolve $resourceDescription for: $url")
        )
        return Result.success(resource)
    }

    private fun resolveMappedResource(url: String): T? {
        return resolvedResources[url] ?: mapper(url)?.also { resolvedResources.put(url, it) }
    }
}

private fun String.hasScheme(expected: String): Boolean =
    Uri.parse(this).scheme.equals(expected, ignoreCase = true)

private const val RESOLVED_RESOURCE_CACHE_SIZE = 100
