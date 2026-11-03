package com.yandex.div.compose.preload

import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.di.DivContextScope
import dev.zacsweers.metro.Inject

@DivContextScope
internal class CustomResourcePreloader @Inject constructor(
    private val factories: Map<String, @JvmSuppressWildcards DivCustomViewFactory>,
) {
    suspend fun preload(environment: DivCustomEnvironment): PreloadResult =
        factories[environment.data.customType]?.preloadWithResult(environment) ?: PreloadResult(true)
}
