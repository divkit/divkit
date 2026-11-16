package com.yandex.div.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.node.CompositionLocalConsumerModifierNode
import androidx.compose.ui.node.currentValueOf
import androidx.compose.ui.platform.LocalContext

/**
 * Android bridge for the renderer's context access. Reuses the platform local's key instead of
 * introducing a computed local, so reads perform a single lookup and follow nested providers.
 */
internal object LocalDivContext {
    val current: DivContext
        @Composable
        @ReadOnlyComposable
        inline get() = LocalContext.current as? DivContext ?: error(MISSING_CONTEXT)

    infix fun provides(context: DivContext): ProvidedValue<*> = LocalContext provides context

    fun currentValueOf(node: CompositionLocalConsumerModifierNode): DivContext =
        node.currentValueOf(LocalContext) as? DivContext ?: error(MISSING_CONTEXT)
}

private const val MISSING_CONTEXT =
    "DivContext not provided. Supply it through LocalContext or use DivViewHost."
