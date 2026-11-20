package com.yandex.div.compose.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection

internal val isRtlLayout: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalLayoutDirection.current == LayoutDirection.Rtl
