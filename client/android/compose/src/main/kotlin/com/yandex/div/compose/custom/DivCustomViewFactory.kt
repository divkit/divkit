package com.yandex.div.compose.custom

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.preload.PreloadResult

/**
 * Factory for composing `div-custom` elements.
 *
 * Implement this interface and pass it to [com.yandex.div.compose.DivConfiguration]
 * to provide custom component rendering.
 *
 * For wrapping classic Android [View], extend [DivCustomAndroidViewFactory] instead.
 *
 * @see com.yandex.div.compose.DivConfiguration
 */
interface DivCustomViewFactory {

    /**
     * Composes the custom element.
     */
    @Composable
    fun Content(modifier: Modifier, environment: DivCustomEnvironment)

    /**
     * Preloads resources required by the custom element before it is shown.
     */
    @Deprecated(
        message = "Use preloadWithResult instead.",
        replaceWith = ReplaceWith("preloadWithResult(environment)"),
    )
    suspend fun preload(environment: DivCustomEnvironment): Unit = Unit

    /**
     * Preloads resources required by the custom element before it is shown.
     *
     * @return result of preloading required resources.
     */
    @Suppress("DEPRECATION")
    suspend fun preloadWithResult(environment: DivCustomEnvironment): PreloadResult {
        preload(environment)
        return PreloadResult(true)
    }
}
