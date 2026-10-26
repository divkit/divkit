package com.yandex.div.compose.preload

import com.yandex.div.compose.DivConfiguration
import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.animation.AnimationConfiguration
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.storedvalues.DivStoredValuesStorage
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivExtension
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

@DivContextScope
internal class ExtensionPreloader @Inject constructor(
    private val handlers: Map<String, @JvmSuppressWildcards DivExtensionHandler>,
    private val configuration: DivConfiguration,
    private val reporter: DivReporter,
    private val animationConfiguration: AnimationConfiguration,
) {
    suspend fun preloadExtensions(
        div: Div,
        resolver: ExpressionResolver,
        storedValuesStorage: DivStoredValuesStorage,
    ): PreloadResult = coroutineScope {
        val extensions: List<Pair<DivExtension, DivExtensionHandler>> =
            div.value().extensions.orEmpty().mapNotNull { extension ->
                handlers[extension.id]?.let { extension to it }
            } + configuration.wildcardExtensions
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
