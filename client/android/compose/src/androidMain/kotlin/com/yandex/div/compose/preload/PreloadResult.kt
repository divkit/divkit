package com.yandex.div.compose.preload

/**
 * Result of preloading resources.
 *
 * @property isSuccessful whether every selected resource was preloaded successfully.
 */
data class PreloadResult(
    val isSuccessful: Boolean,
)

internal fun Iterable<PreloadResult>.combineResults(): PreloadResult =
    PreloadResult(all { it.isSuccessful })
