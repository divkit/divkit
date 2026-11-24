package com.yandex.div.compose.tooltips

import androidx.compose.runtime.Composable

internal interface TooltipDrawObserverFactory {

    @Composable
    fun rememberObserver(): TooltipDrawObserver
}

internal fun interface TooltipDrawObserver {
    fun observe(onDraw: () -> Unit): () -> Unit
}
