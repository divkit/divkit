package com.yandex.div.core.preload

import android.net.Uri

/**
 * Result of a preload.
 */
sealed interface PreloadResult

/**
 * Result of a preload for a single URI.
 */
class UriPreloadResult(
    val uri: Uri,
    val error: Throwable?
): PreloadResult

/**
 * Holds composition of preload results.
 */
class CompositeResult(
    val results: List<PreloadResult>,
): PreloadResult


internal fun PreloadResult.filterErrorResults(): Sequence<UriPreloadResult> {
    val result = this
    return sequence {
        when (result) {
            is CompositeResult -> result.results.forEach {
                yieldAll(it.filterErrorResults())
            }
            is UriPreloadResult -> {
                if (result.error != null) {
                    yield(result)
                }
            }
        }
    }
}
