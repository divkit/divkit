package com.yandex.div.compose.preload

import com.yandex.div.compose.custom.DivCustomEnvironment
import com.yandex.div.compose.custom.DivCustomViewFactory
import com.yandex.div.compose.dagger.DivContextScope
import javax.inject.Inject

@DivContextScope
internal class CustomResourcePreloader @Inject constructor(
    private val factories: Map<String, @JvmSuppressWildcards DivCustomViewFactory>,
) {
    suspend fun preload(environment: DivCustomEnvironment) =
        factories[environment.data.customType]?.preload(environment)
}
