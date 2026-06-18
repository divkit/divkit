package com.yandex.div.compose.preload

import com.yandex.div.compose.DivReporter
import com.yandex.div.compose.dagger.DivContextScope
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import javax.inject.Inject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@DivContextScope
internal class ExtensionPreloader @Inject constructor(
    private val handlers: Map<String, @JvmSuppressWildcards DivExtensionHandler>,
    private val reporter: DivReporter,
) {
    suspend fun preloadExtensions(div: Div, resolver: ExpressionResolver) = coroutineScope {
        val extensions = div.value().extensions ?: return@coroutineScope
        if (extensions.isEmpty() || handlers.isEmpty()) return@coroutineScope
        extensions.forEach { extension ->
            val handler = handlers[extension.id] ?: return@forEach
            val environment = DivExtensionEnvironment(
                data = div,
                extension = extension,
                expressionResolver = resolver,
                reporter = reporter,
            )
            launch { handler.preload(environment) }
        }
    }
}
