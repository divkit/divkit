package com.yandex.div.compose.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Applies `div-extension` to the UI elements.
 *
 * @see com.yandex.div.compose.DivConfiguration
 */
interface DivExtensionHandler {

    /**
     * Composes the element.
     */
    @Composable
    fun Content(
        modifier: Modifier,
        environment: DivExtensionEnvironment,
        content: @Composable (modifier: Modifier) -> Unit
    )

    /**
     * Preloads resources required by the extension before the element is shown.
     */
    suspend fun preload(environment: DivExtensionEnvironment) = Unit
}
