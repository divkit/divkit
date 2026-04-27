package com.yandex.div.compose.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.core.annotations.ExperimentalApi

/**
 * Applies `div-extension` to the UI elements.
 *
 * @see com.yandex.div.compose.DivComposeConfiguration
 */
@ExperimentalApi
interface DivExtensionHandler {

    /**
     * Composes the element.
     */
    @Composable
    fun Content(
        environment: DivExtensionEnvironment,
        content: @Composable (modifier: Modifier) -> Unit
    )
}
