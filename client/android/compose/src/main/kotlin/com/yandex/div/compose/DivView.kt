package com.yandex.div.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.dagger.LocalComponent
import com.yandex.div.compose.triggers.observe
import com.yandex.div.compose.utils.divContext
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div2.DivData

/**
 * A composable that builds its own layout according to the provided [DivData].
 *
 * Requires [DivContext] to be set as the current [android.content.Context] in the composition.
 * The simplest way to achieve this is to use [DivContext] as the context for
 * [androidx.compose.ui.platform.ComposeView]:
 *
 *    val configuration = DivComposeConfiguration()
 *    val divContext = configuration.createContext(baseContext = activity)
 *    ComposeView(divContext).setContent {
 *        DivView(data = data)
 *    }
 *
 * @param data the [DivData] describing the layout to render.
 * @param modifier the [Modifier] to be applied to the root layout.
 */
@ExperimentalApi
@Composable
fun DivView(
    data: DivData,
    modifier: Modifier = Modifier
) {
    val viewContext = divContext.getViewContext(data)
    val localComponent = viewContext.rootLocalComponent
    localComponent.triggerStorage.observe()
    CompositionLocalProvider(
        LocalDivViewContext provides viewContext,
        LocalComponent provides localComponent
    ) {
        val states = data.states
        if (states.size > 1) {
            reportError("Multiple root states not supported")
        }

        val div = states.firstOrNull()?.div
        if (div == null) {
            reportError("Empty data")
            return@CompositionLocalProvider
        }

        DivBlockView(
            data = div,
            modifier = modifier
        )
    }
}
