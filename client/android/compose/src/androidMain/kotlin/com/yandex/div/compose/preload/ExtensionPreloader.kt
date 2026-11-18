package com.yandex.div.compose.preload

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.animation.AnimationConfiguration
import com.yandex.div.compose.di.DivContextScope
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.storedvalues.DivStoredValuesStorage
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivExtension
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

@DivContextScope
internal class ExtensionPreloader @Inject constructor(
    private val animationConfiguration: AnimationConfiguration,
    private val extensionHandlers: Map<String, @JvmSuppressWildcards DivExtensionHandler>,
    private val reporter: DivReporter,
    private val wildcardExtensions: List<Pair<DivExtension, DivExtensionHandler>>,
) {
    suspend fun preloadExtensions(
        div: Div,
        resolver: ExpressionResolver,
        storedValuesStorage: DivStoredValuesStorage,
    ): PreloadResult = coroutineScope {
        val extensions = div.value().extensions.orEmpty()
            .mapNotNull { extension ->
                extensionHandlers[extension.id]?.let { extension to it }
            } + wildcardExtensions
        extensions.map { (extension, handler) ->
            val environment = DivExtensionEnvironment(
                data = div,
                extension = extension,
                expressionResolver = resolver,
                reporter = reporter,
                animationsEnabled = animationConfiguration.isEnabled,
                storedValuesStorage = storedValuesStorage,
            )
            async { handler.preloadWithResult(environment) }
        }.awaitAll().combineResults()
    }
}
