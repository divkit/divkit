package com.yandex.div.compose.views

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.expressions.observedValue
import com.yandex.div.compose.extensions.DivExtensionEnvironment
import com.yandex.div.compose.extensions.DivExtensionHandler
import com.yandex.div.compose.utils.reportError
import com.yandex.div2.Div
import com.yandex.div2.DivExtension

internal class DivContentDecorator(
    private val data: Div,
    private val decoration: Modifier = Modifier,
) {

    @Composable
    fun Content(modifier: Modifier, content: @Composable (Modifier) -> Unit) {
        val component = divContext.component
        val extensions = data.value().extensions
            ?.filter { it.isEnabled.observedValue() }
            ?.map { it to component.extensionHandlers[it.id] }
            .orEmpty()
        WithExtensions(
            extensions = component.wildcardExtensions + extensions,
            modifier = modifier.then(decoration),
            content = content
        )
    }

    @Composable
    private fun WithExtensions(
        extensions: List<Pair<DivExtension, DivExtensionHandler?>>,
        modifier: Modifier,
        content: @Composable (Modifier) -> Unit,
    ) {
        val first = extensions.firstOrNull()
        if (first == null) {
            content(modifier)
            return
        }

        val (extension, extensionHandler) = first
        if (extensionHandler == null) {
            reportError("No handler for extension: ${extension.id}")
            WithExtensions(
                extensions = extensions.drop(1),
                modifier = modifier,
                content = content,
            )
            return
        }

        val component = LocalComponent.current
        extensionHandler.Content(
            modifier = modifier,
            environment = DivExtensionEnvironment(
                data = data,
                extension = extension,
                expressionResolver = component.expressionResolver,
                reporter = component.reporter,
                animationsEnabled = component.animationConfiguration.isEnabledAsState(),
                storedValuesStorage = component.storedValuesStorage,
            ),
            content = { modifier ->
                WithExtensions(
                    extensions = extensions.drop(1),
                    modifier = modifier,
                    content = content,
                )
            }
        )
    }
}
