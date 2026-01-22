package com.yandex.div.histogram.metrics

import kotlin.math.max

internal class RenderMetrics {

    var bindingDuration: Long = 0
        private set
    var rebindingDuration: Long = 0
        private set
    var measureDuration: Long = 0
        private set
    var layoutDuration: Long = 0
        private set
    var drawDuration: Long = 0
        private set

    val totalDuration: Long
        get() = max(bindingDuration, rebindingDuration) + measureDuration + layoutDuration + drawDuration

    fun binding(duration: Long) {
        bindingDuration = duration
    }

    fun rebinding(duration: Long) {
        rebindingDuration = duration
    }

    fun addMeasure(duration: Long) {
        measureDuration += duration
    }

    fun addLayout(duration: Long) {
        layoutDuration += duration
    }

    fun addDraw(duration: Long) {
        drawDuration += duration
    }

    fun reset() {
        measureDuration = 0
        layoutDuration = 0
        drawDuration = 0
        bindingDuration = 0
        rebindingDuration = 0
    }
}
