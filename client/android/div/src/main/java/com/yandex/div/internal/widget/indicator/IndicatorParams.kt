package com.yandex.div.internal.widget.indicator

import android.graphics.Color


internal class IndicatorParams {

    data class Style(
        val animation: Animation,
        val activeShape: Shape,
        val inactiveShape: Shape,
        val minimumShape: Shape,
        val itemsPlacement: ItemPlacement
    )

    sealed class Shape {

        abstract val color: Int
        abstract val itemSize: ItemSize

        data class RoundedRect(
            override val color: Int,
            override val itemSize: ItemSize.RoundedRect,
            val strokeWidth: Float,
            val strokeColor: Int
        ): Shape()

        data class Circle(
            override val color: Int,
            override val itemSize: ItemSize.Circle
        ): Shape()

        val borderWidth get() = when (this) {
            is RoundedRect -> strokeWidth
            else -> 0f
        }
        val borderColor get() = when (this) {
            is RoundedRect -> strokeColor
            else -> Color.TRANSPARENT
        }
    }

    sealed class ItemSize {

        data class RoundedRect(
            var itemWidth: Float,
            var itemHeight: Float,
            var cornerRadius: Float,
        ): ItemSize()

        data class Circle (
            var radius: Float,
        ): ItemSize()

        val width get() = when (this) {
            is RoundedRect -> itemWidth
            is Circle -> radius * 2
        }

        val height get() = when (this) {
            is RoundedRect -> itemHeight
            is Circle -> radius * 2
        }
    }

    sealed interface ItemPlacement {
        data class Default(
            val spaceBetweenCenters: Float,
        ) : ItemPlacement

        data class Stretch(
            val itemSpacing: Float,
            val maxVisibleItems: Int,
        ) : ItemPlacement
    }

    enum class Animation {
        SCALE, WORM, SLIDER
    }
}
