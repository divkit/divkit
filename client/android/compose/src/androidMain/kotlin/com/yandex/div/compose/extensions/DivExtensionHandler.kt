package com.yandex.div.compose.extensions

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.preload.PreloadResult

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
    @Deprecated(
        message = "Use preloadWithResult instead.",
        replaceWith = ReplaceWith("preloadWithResult(environment)"),
    )
    suspend fun preload(environment: DivExtensionEnvironment): Unit = Unit

    /**
     * Preloads resources required by the extension before the element is shown.
     *
     * @return result of preloading required resources.
     */
    @Suppress("DEPRECATION")
    suspend fun preloadWithResult(environment: DivExtensionEnvironment): PreloadResult {
        preload(environment)
        return PreloadResult(true)
    }
}
