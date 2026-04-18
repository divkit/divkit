package com.yandex.div.compose.custom

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.core.annotations.ExperimentalApi

/**
 * Factory for rendering `div-custom` elements in Compose.
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

    @Composable
    fun Content(
        environment: DivCustomEnvironment,
        modifier: Modifier,
    )
}
