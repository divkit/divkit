package com.yandex.div.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yandex.div.compose.utils.reporter
import com.yandex.div.compose.views.DivBlockView
import com.yandex.div.compose.views.WithLocalDivContext
import com.yandex.div2.DivData

/**
 * A composable that builds its own layout according to the provided [DivData]. Must be used within
 * the [DivContext].
 *
 * Example usage:
 *
 *    ComposeView(divContext).setContent {
 *        DivView(data = data)
 *    }
 *
 * or
 *
 *    CompositionLocalProvider(LocalContext provides divContext) {
 *        DivView(data = data)
 *    }
 */
@Composable
fun DivView(
    data: DivData,
    modifier: Modifier = Modifier
) {
    val states = data.states
    if (states.size > 1) {
        reporter.reportError("Multiple root states not supported")
    }

    val div = states.firstOrNull()?.div
    if (div == null) {
        reporter.reportError("Empty data")
        return
    }

    WithLocalDivContext(data) {
        DivBlockView(
            data = div,
            modifier = modifier
        )
    }
}
