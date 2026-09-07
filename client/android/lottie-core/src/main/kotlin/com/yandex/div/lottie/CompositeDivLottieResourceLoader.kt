package com.yandex.div.lottie

import android.util.LruCache
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieResult

/**
 * Delegates each URL to the first [DivLottieResourceLoader] that claims it.
 *
 * Loader order is significant. A result, including a failure, is terminal and is not passed to
 * subsequent loaders.
 */
public class CompositeDivLottieResourceLoader(
    loaders: List<DivLottieResourceLoader>,
) : DivLottieResourceLoader {

    private val loaders = loaders.toList()
    private val resolvedLoaders = LruCache<String, DivLottieResourceLoader>(RESOLVED_LOADER_CACHE_SIZE)

    public constructor(
        vararg loaders: DivLottieResourceLoader,
    ) : this(loaders.asList())

    override fun canLoad(url: String): Boolean = resolveLoader(url) != null

    override suspend fun load(url: String): LottieResult<LottieComposition> {
        val loader = resolveLoader(url)
            ?: return LottieResult(IllegalArgumentException("No Lottie resource loader for: $url"))
        return loader.load(url)
    }

    @Synchronized
    private fun resolveLoader(url: String): DivLottieResourceLoader? {
        return resolvedLoaders[url]
            ?: loaders.firstOrNull { it.canLoad(url) }?.also { resolvedLoaders.put(url, it) }
    }
}

private const val RESOLVED_LOADER_CACHE_SIZE = 100
