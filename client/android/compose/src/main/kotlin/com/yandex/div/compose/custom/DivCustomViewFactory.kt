package com.yandex.div.compose.custom

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.core.annotations.ExperimentalApi

/**
 * Factory for composing `div-custom` elements.
 *
 * Implement this interface and pass it to [com.yandex.div.compose.DivComposeConfiguration]
 * to provide custom component rendering.
 *
 * For wrapping classic Android [View], extend [DivCustomAndroidViewFactory] instead.
 *
 * @see com.yandex.div.compose.DivComposeConfiguration
 */
@ExperimentalApi
interface DivCustomViewFactory {

    /**
     * Composes the custom element.
     */
    @Composable
    fun Content(modifier: Modifier, environment: DivCustomEnvironment)

    /**
     * Preloads resources required by the custom element before it is shown.
     */
    suspend fun preload(environment: DivCustomEnvironment) = Unit
}
