package com.yandex.div.lottie

import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieResult
import kotlinx.coroutines.CancellationException

/**
 * Loads Lottie compositions from URLs owned by the host application.
 *
 * When [canLoad] returns `true`, [load] is the terminal source for that URL. The loader is
 * responsible for parsing, caching and deduplicating its own resources, and [load] must be safe
 * to call from the main thread. Cancellation must be thrown as
 * [kotlinx.coroutines.CancellationException], not returned inside [LottieResult].
 */
public interface DivLottieResourceLoader {
    /** Returns whether this loader owns [url]. This method must be fast and side-effect free. */
    public fun canLoad(url: String): Boolean

    /** Loads the composition for [url]. */
    public suspend fun load(url: String): LottieResult<LottieComposition>
}

/**
 * Loads [url], converting ordinary thrown failures to [LottieResult] while preserving coroutine
 * cancellation. This keeps the cancellation contract identical for every renderer.
 */
public suspend fun DivLottieResourceLoader.loadComposition(
    url: String,
): LottieResult<LottieComposition> {
    val result = try {
        load(url)
    } catch (error: CancellationException) {
        throw error
    } catch (error: Exception) {
        return LottieResult(error)
    }
    val error = result.exception
    if (error is CancellationException) throw error
    return result
}
