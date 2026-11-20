package com.yandex.div.compose.utils

import androidx.compose.ui.Modifier

internal inline fun Modifier.applyIf(
    condition: Boolean,
    block: Modifier.() -> Modifier,
): Modifier = if (condition) block() else this

internal inline fun <T : Any> Modifier.applyIfNotNull(
    value: T?,
    block: Modifier.(T) -> Modifier,
): Modifier = if (value != null) block(value) else this
