package com.yandex.div.core.widget.indicator

class IndicatorParams {

    data class Style(
        var color: Int,
        var selectedColor: Int,

        var normalWidth: Float,
        var selectedWidth: Float,
        var minimumWidth: Float,

        var normalHeight: Float,
        var selectedHeight: Float,
        var minimumHeight: Float,

        var cornerRadius: Float,
        var selectedCornerRadius: Float,
        var minimumCornerRadius: Float,
        var spaceBetweenCenters: Float,

        var animation: Animation,
        var shape: Shape
    )

    enum class Shape {
        ROUND_RECT
    }

    enum class Animation {
        SCALE, WORM, SLIDER
    }
}
