package com.yandex.div.compose.context

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import com.yandex.div.compose.DivContext
import com.yandex.div.compose.LocalDivContext

internal val divContext: DivContext
    @Composable
    @ReadOnlyComposable
    get() = LocalDivContext.current
