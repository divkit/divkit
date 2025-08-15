package com.yandex.div.core.widget

internal interface AspectView {
    var aspectRatio: Float

    companion object {
        const val DEFAULT_ASPECT_RATIO = 0f

        internal fun aspectRatioProperty() =
            dimensionAffecting(DEFAULT_ASPECT_RATIO) { it.coerceAtLeast(DEFAULT_ASPECT_RATIO) }
    }
}
