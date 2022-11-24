package com.yandex.div.internal.widget.indicator

internal class IndicatorParams {

    data class Style(
        val color: Int,
        val selectedColor: Int,
        val animation: Animation,
        val shape: Shape,
        val itemsPlacement: ItemPlacement
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
        ): Shape() {
            val minimumItemSizeRect = ItemSize.RoundedRect(
                itemWidth = this.minimumWidth,
                itemHeight = this.minimumHeight,
                cornerRadius = this.minimumCornerRadius,
            )
            val normalItemSizeRect = ItemSize.RoundedRect(
                itemWidth = this.normalWidth,
                itemHeight = this.normalHeight,
                cornerRadius = this.cornerRadius,
            )
        }

        data class Circle(
            val normalRadius: Float,
            val selectedRadius: Float,
            val minimumRadius: Float
        ): Shape() {
            val minimumItemSizeCircle = ItemSize.Circle(
                radius = this.minimumRadius,
            )
            val normalItemSizeCircle = ItemSize.Circle(
                radius = this.normalRadius,
            )
        }

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

        val minimumItemSize get() = when (this) {
            is RoundedRect -> this.minimumItemSizeRect
            is Circle -> this.minimumItemSizeCircle
        }

        val normalItemSize get() = when(this) {
            is RoundedRect -> this.normalItemSizeRect
            is Circle -> this.normalItemSizeCircle
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
