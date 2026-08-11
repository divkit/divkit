package com.yandex.div.compose.lottie

/**
 * Network cache for Lottie compositions
 */
interface LottieNetworkCache {

    /**
     * @return cached lottie composition as JSON string, or null if not cached
     */
    fun get(url: String): String?

    /**
     * Fetches and caches lottie composition from network
     */
    suspend fun save(url: String)

    companion object {
        val STUB: LottieNetworkCache = object : LottieNetworkCache {
            override fun get(url: String): String? = null
            override suspend fun save(url: String) = Unit
        }
    }
}
