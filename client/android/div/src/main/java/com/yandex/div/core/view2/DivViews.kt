package com.yandex.div.core.view2

import android.graphics.Outline
import android.view.ViewGroup
import androidx.core.view.allViews

/**
 * Disables [Outline] clipping for this [Div2View] and calls the specified function [block].
 * Useful in cases when clipping needs to be applied directly to the canvas.
 * A typical case is to take view snapshot by `view.draw(canvas)`.
 */
public inline fun <T> Div2View.withCanvasClipping(block: () -> T): T {
    return withDivViewCanvasClipping(block)
}

/**
 * Disables [Outline] clipping for all descendant [Div2Views][Div2View] and calls the specified function [block].
 * Use cases is the same as for [withCanvasClipping].
 */
public inline fun <T> ViewGroup.withDivViewCanvasClipping(block: () -> T): T {
    val divViews = allViews.filterIsInstance<Div2View>().toList()
    divViews.forEach { divView -> divView.forceCanvasClipping = true }
    try {
        return block()
    } finally {
        divViews.forEach { divView -> divView.forceCanvasClipping = false }
    }
}
