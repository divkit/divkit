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

    /**
     * Requests caching lottie composition from network with result.
     * @return true if caching callbacks supported.
     */
    fun cacheComposition(url: String, onComplete: ((Throwable?) -> Unit)): Boolean {
        return false
    }

    companion object {
        @JvmStatic
        val STUB = object : DivLottieNetworkCache {
            override fun loadCached(url: String): String? = null
            override fun cacheComposition(url: String) = Unit
        }
    }
}
