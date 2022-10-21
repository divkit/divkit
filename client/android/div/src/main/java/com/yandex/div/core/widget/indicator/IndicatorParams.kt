package com.yandex.div.core.widget.indicator

class IndicatorParams {

    data class Style(
        val color: Int,
        val selectedColor: Int,
        val spaceBetweenCenters: Float,
        val animation: Animation,
        val shape: Shape,
    )

    sealed class Shape {

        data class RoundedRect(
            val normalWidth: Float,
            val selectedWidth: Float,
            val minimumWidth: Float,

            val normalHeight: Float,
            val selectedHeight: Float,
            val minimumHeight: Float,

            val cornerRadius: Float,
            val selectedCornerRadius: Float,
            val minimumCornerRadius: Float
        ): Shape()

        data class Circle(
            val normalRadius: Float,
            val selectedRadius: Float,
            val minimumRadius: Float
        ): Shape()

        val width get() = when (this) {
            is RoundedRect -> selectedWidth
            is Circle -> selectedRadius * 2
        }

        val minimumSize get() = when(this) {
            is RoundedRect -> minimumWidth
            is Circle -> minimumRadius * 2
        }

        val height get() = when (this) {
            is RoundedRect -> selectedHeight
            is Circle -> selectedRadius * 2
        }

        val minimumItemSize get(): ItemSize = when (this) {
            is RoundedRect -> ItemSize.RoundedRect(
                minimumWidth, minimumHeight, minimumCornerRadius
            )
            is Circle -> ItemSize.Circle(
                minimumRadius
            )
        }

        val normalItemSize get(): ItemSize = when (this) {
            is RoundedRect -> ItemSize.RoundedRect(
                normalWidth, normalHeight, cornerRadius
            )
            is Circle -> ItemSize.Circle(
                normalRadius
            )
        }
    }

    sealed class ItemSize {

        data class RoundedRect(
            val itemWidth: Float,
            val itemHeight: Float,
            val cornerRadius: Float,
        ): ItemSize()

        data class Circle (
            val radius: Float,
        ): ItemSize()

        val width get() = when (this) {
            is RoundedRect -> itemWidth
            is Circle -> radius * 2
        }
    }

    enum class Animation {
        SCALE, WORM, SLIDER
    }
}
