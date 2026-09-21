package com.yandex.div.compose.preload

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.animation.AnimationConfiguration
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

@DivContextScope
internal class ExtensionPreloader @Inject constructor(
    private val handlers: Map<String, @JvmSuppressWildcards DivExtensionHandler>,
    private val reporter: DivReporter,
    private val animationConfiguration: AnimationConfiguration,
) {
    suspend fun preloadExtensions(div: Div, resolver: ExpressionResolver): PreloadResult = coroutineScope {
        val extensions = div.value().extensions ?: return@coroutineScope PreloadResult(true)
        if (extensions.isEmpty() || handlers.isEmpty()) return@coroutineScope PreloadResult(true)
        extensions.mapNotNull { extension ->
            val handler = handlers[extension.id] ?: return@mapNotNull null
            val environment = DivExtensionEnvironment(
                data = div,
                extension = extension,
                expressionResolver = resolver,
                reporter = reporter,
                animationsEnabled = animationConfiguration.isEnabled,
            )
            async { handler.preloadWithResult(environment) }
        }.awaitAll().combineResults()
    }
}
