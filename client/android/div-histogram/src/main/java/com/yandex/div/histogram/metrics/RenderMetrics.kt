package com.yandex.div.histogram.metrics

import kotlin.math.max

internal class RenderMetrics {

    var bindingMs: Long = 0
        private set
    var rebindingMs: Long = 0
        private set
    var measureMs: Long = 0
        private set
    var layoutMs: Long = 0
        private set
    var drawMs: Long = 0
        private set

    val totalMs: Long
        get() = max(bindingMs, rebindingMs) + measureMs + layoutMs + drawMs

    fun binding(duration: Long) {
        bindingMs = duration
    }

    fun rebinding(duration: Long) {
        rebindingMs = duration
    }

    fun addMeasure(duration: Long) {
        measureMs += duration
    }

    fun addLayout(duration: Long) {
        layoutMs += duration
    }

    fun addDraw(duration: Long) {
        drawMs += duration
    }

    fun reset() {
        measureMs = 0
        layoutMs = 0
        drawMs = 0
        bindingMs = 0
        rebindingMs = 0
    }
}
