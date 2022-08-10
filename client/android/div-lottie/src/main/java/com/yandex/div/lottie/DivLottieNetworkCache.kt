package com.yandex.div.lottie

/**
 * Network cache for [com.airbnb.lottie.LottieComposition]
 */
interface DivLottieNetworkCache {

    /**
     * @returns lottie composition as json string if it was cached,
     * null otherwise
     */
    fun loadCached(url: String): String?

    /**
     * Requests caching lottie composition from network
     */
    fun cacheComposition(url: String)

    companion object {
        @JvmStatic
        val STUB = object : DivLottieNetworkCache {
            override fun loadCached(url: String): String? = null
            override fun cacheComposition(url: String) = Unit
        }
    }
}
