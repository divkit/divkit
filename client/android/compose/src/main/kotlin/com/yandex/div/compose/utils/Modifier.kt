package com.yandex.div.compose.utils

import androidx.compose.ui.Modifier

internal inline fun Modifier.applyIf(
    condition: Boolean,
    block: Modifier.() -> Modifier,
): Modifier = if (condition) block() else this