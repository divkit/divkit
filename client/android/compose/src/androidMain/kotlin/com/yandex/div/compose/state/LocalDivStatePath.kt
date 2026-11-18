package com.yandex.div.compose.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.yandex.div.compose.DivException
import com.yandex.div.core.state.DivStatePath

internal val LocalDivStatePath = staticCompositionLocalOf<DivStatePath> {
    throw DivException("LocalDivStatePath not provided")
}

@Composable
internal fun WithRootStatePath(rootStateId: Long, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalDivStatePath provides DivStatePath.fromState(rootStateId),
        content
    )
}
