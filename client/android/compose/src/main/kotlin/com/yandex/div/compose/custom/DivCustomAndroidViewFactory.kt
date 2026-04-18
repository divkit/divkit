package com.yandex.div.compose.custom

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.div.core.annotations.ExperimentalApi

/**
 * Abstract [DivCustomViewFactory] that wraps a classic Android [View]
 * using `androidx.compose.ui.viewinterop.AndroidView`.
 *
 * Subclasses provide [createView] / [bindView] callbacks and optionally override [release]
 * to clean up resources when the view leaves the composition.
 */
@ExperimentalApi
abstract class DivCustomAndroidViewFactory : DivCustomViewFactory {

    abstract fun createView(environment: DivCustomEnvironment): View

    open fun bindView(view: View, environment: DivCustomEnvironment) = Unit

    open fun release(view: View) = Unit

    @Composable
    final override fun Content(
        environment: DivCustomEnvironment,
        modifier: Modifier,
    ) {
        key(environment.div.customType) {
            AndroidView(
                factory = { createView(environment) },
                modifier = modifier,
                update = { bindView(it, environment) },
                onRelease = { release(it) },
            )
        }
    }
}
