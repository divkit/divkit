package com.yandex.div.compose.interop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import com.yandex.div.core.Div2Context

public val LocalDivContext: ProvidableCompositionLocal<Div2Context> = staticCompositionLocalOf {
    noLocalDivContextProvided()
}

@Composable
public fun Div2Context.scoped(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalDivContext provides this, content)
}

private fun noLocalDivContextProvided(): Nothing {
    error("LocalDivContext not found. Make sure 'Div2Context.scoped()' was called.")
}
