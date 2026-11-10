package com.yandex.div.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.yandex.div.compose.context.LocalDivViewContext
import com.yandex.div.compose.context.divContext
import com.yandex.div.compose.di.LocalComponent
import com.yandex.div.compose.state.WithRootStatePath
import com.yandex.div.compose.tooltips.TooltipsHost
import com.yandex.div.compose.utils.reportError
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.modifiers.bindRootFocus
import com.yandex.div2.DivData
import kotlin.time.measureTimedValue

/**
 * A composable that builds its own layout according to the provided [DivData].
 *
 * Requires [DivContext] to be provided through [LocalDivContext]:
 *
 *    val configuration = divConfiguration {}
 *    val divContext = DivContext(activity, configuration)
 *    ComposeView(activity).setContent {
 *        CompositionLocalProvider(LocalDivContext provides divContext) {
 *            DivView(data = data)
 *        }
 *    }
 *
 * @param data the [DivData] describing the layout to render.
 * @param modifier the [Modifier] to be applied to the root layout.
 */
@Composable
fun DivView(
    data: DivData,
    modifier: Modifier = Modifier
) {
    val (viewContext, prepareDuration) = measureTimedValue { divContext.getViewContext(data) }
    viewContext.component.histogramReporter.measure(prepareDuration) {
        val localComponent = viewContext.rootLocalComponent
        val boundModifier = modifier.bindRootFocus(viewContext.component.focusBindingStorage)
        viewContext.onComposition()

        CompositionLocalProvider(
            LocalDivViewContext provides viewContext,
            LocalComponent provides localComponent
        ) {
            val states = viewContext.states
            if (states.size > 1) {
                reportError("Multiple root states not supported")
            }

            val rootState = states.firstOrNull()
            if (rootState == null) {
                reportError("Empty data")
                return@CompositionLocalProvider
            }

            WithRootStatePath(rootState.stateId) {
                DivBlockView(
                    data = rootState.div,
                    modifier = boundModifier
                )
                TooltipsHost()
            }
        }
    }
}
