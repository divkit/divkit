package com.yandex.div.internal.widget.indicator

import android.graphics.Canvas
import android.view.View
import com.yandex.div.internal.widget.indicator.animations.IndicatorAnimator
import com.yandex.div.internal.widget.indicator.forms.SingleIndicatorDrawer
import com.yandex.div.core.util.isLayoutRtl

internal class IndicatorsStripDrawer(
    private val styleParams: IndicatorParams.Style,
    private val singleIndicatorDrawer: SingleIndicatorDrawer,
    private val animator: IndicatorAnimator,
    private val view: View,
) {
    private val ribbon = IndicatorsRibbon()

    private var itemsCount: Int = 0
    private var maxVisibleCount: Int = 0

    private var baseYOffset: Float = styleParams.inactiveShape.itemSize.width
    private var spaceBetweenCenters: Float = 0f
    private var itemWidthMultiplier: Float = 1f

    private var viewportWidth: Int = 0
    private var viewportHeight: Int = 0
    private var selectedItemPosition: Int = 0
    private var selectedItemFraction: Float = 0f

    fun onPageScrolled(position: Int, positionFraction: Float) {
        selectedItemPosition = position
        selectedItemFraction = positionFraction
        animator.onPageScrolled(position, positionFraction)
        adjustVisibleItems(position, positionFraction)
    }

    fun onPageSelected(position: Int) {
        selectedItemPosition = position
        selectedItemFraction = 0f
        animator.onPageSelected(position)
        adjustVisibleItems(position, 0f)
    }

    fun onDraw(canvas: Canvas) {
        ribbon.visibleItems.forEach { indicator ->
            singleIndicatorDrawer.draw(
                    canvas,
                    indicator.centerOffset,
                    baseYOffset,
                    indicator.itemSize,
                    animator.getColorAt(indicator.position),
                    animator.getBorderWidthAt(indicator.position),
                    animator.getBorderColorAt(indicator.position)
            )
        }

        ribbon.visibleItems.find { it.active }?.let { indicator ->
            val rect = animator.getSelectedItemRect(indicator.centerOffset, baseYOffset,
                viewportWidth.toFloat(), view.isLayoutRtl())
            if (rect != null) {
                singleIndicatorDrawer.drawSelected(canvas, rect)
            }
        }
    }

    fun setItemsCount(count: Int) {
        itemsCount = count
        animator.setItemsCount(count)

        calculateMaximumVisibleItems()
        baseYOffset = viewportHeight / 2f
    }

    fun calculateMaximumVisibleItems(viewportWidth: Int, viewportHeight: Int) {
        if (viewportWidth == 0 || viewportHeight == 0) return
        this.viewportWidth = viewportWidth
        this.viewportHeight = viewportHeight

        calculateMaximumVisibleItems()
        adjustItemsPlacement()

        baseYOffset = viewportHeight / 2f
        adjustVisibleItems(selectedItemPosition, selectedItemFraction)
    }

    fun getMaxVisibleItems() = maxVisibleCount

    private fun calculateMaximumVisibleItems() {
        maxVisibleCount = when (val itemPlacement = styleParams.itemsPlacement) {
            is IndicatorParams.ItemPlacement.Default -> ((viewportWidth / itemPlacement.spaceBetweenCenters).toInt())
            is IndicatorParams.ItemPlacement.Stretch -> itemPlacement.maxVisibleItems
        }.coerceAtMost(itemsCount)
    }

    private fun adjustVisibleItems(position: Int, positionOffset: Float) {
        ribbon.relayout(position, positionOffset)
    }

    private fun adjustItemsPlacement() {
        when (val itemPlacement = styleParams.itemsPlacement) {
            is IndicatorParams.ItemPlacement.Default -> {
                spaceBetweenCenters = itemPlacement.spaceBetweenCenters
                itemWidthMultiplier = 1f
            }
            is IndicatorParams.ItemPlacement.Stretch -> {
                spaceBetweenCenters = (viewportWidth + itemPlacement.itemSpacing) / maxVisibleCount
                itemWidthMultiplier = (spaceBetweenCenters - itemPlacement.itemSpacing) /
                        styleParams.activeShape.itemSize.width
            }
        }
        animator.updateSpaceBetweenCenters(spaceBetweenCenters)
    }

    private fun getItemSizeAt(position: Int): IndicatorParams.ItemSize {
        var itemSize = animator.getItemSizeAt(position)

        if (itemWidthMultiplier != 1.0f && itemSize is IndicatorParams.ItemSize.RoundedRect) {
            itemSize = itemSize.copy(itemWidth = itemSize.itemWidth * itemWidthMultiplier)
            animator.overrideItemWidth(itemSize.itemWidth)
        }

        return itemSize
    }

    private data class Indicator(
        val position: Int,
        val active: Boolean,
        val centerOffset: Float,
        val itemSize: IndicatorParams.ItemSize,
        val scaleFactor: Float = 1f,
    ) {
        val left: Float get() = centerOffset - itemSize.width / 2f
        val right: Float get() = centerOffset + itemSize.width / 2f
    }

    /**
     * Used to prepare layout of currently visible indicators.
     */
    private inner class IndicatorsRibbon {
        /**
         * List of all items with absolute offsets. Intermediate data
         * from which [visibleItems] are built.
         */
        private val allItems = mutableListOf<Indicator>()

        /**
         * List of visible items with offsets that could be used in real viewport.
         */
        val visibleItems = mutableListOf<Indicator>()

        fun relayout(activePosition: Int, positionFraction: Float) {
            allItems.clear()
            visibleItems.clear()
            if (itemsCount <= 0) {
                return
            }

            val left: Int
            val indicatorsPosition = if (view.isLayoutRtl()) {
                left = itemsCount - 1
                (itemsCount - 1 downTo 0)
            } else {
                left =  0
                (0 until itemsCount)
            }
            indicatorsPosition.forEach { position ->
                val size = getItemSizeAt(position)
                val centerOffset = when (position) {
                    left -> size.width / 2f
                    else -> allItems.last().centerOffset + spaceBetweenCenters
                }

                allItems.add(Indicator(
                        position = position,
                        active = position == activePosition,
                        centerOffset = centerOffset,
                        itemSize = size,
                ))

            }
            visibleItems.addAll(relayoutVisibleItems(activePosition, positionFraction))
        }

        private fun relayoutVisibleItems(activePosition: Int,
                                         positionFraction: Float): List<Indicator> {
            val shiftToCurrentViewport = calcOffsetShiftFor(activePosition, positionFraction)
            val viewportItems = allItems.map {
                it.copy(centerOffset = it.centerOffset + shiftToCurrentViewport)
            }.toMutableList()

            if (viewportItems.size <= maxVisibleCount) {
                return viewportItems
            }

            val viewPort = 0f..viewportWidth.toFloat()

            // Remove alignment to center when first or last item fits to viewport.
            when {
                viewPort.contains(viewportItems.first().left) -> {
                    val offsetToSide = -viewportItems.first().left
                    replaceAll(viewportItems) { it.copy(centerOffset = it.centerOffset + offsetToSide) }
                }
                viewPort.contains(viewportItems.last().right) -> {
                    val offsetToSide = viewportWidth - viewportItems.last().right
                    replaceAll(viewportItems) { it.copy(centerOffset = it.centerOffset + offsetToSide) }
                }
            }

            // Remove items outside viewport
            viewportItems.removeAll {
                !viewPort.contains(it.centerOffset)
            }

            downscaleAndDisperse(viewportItems)
            return viewportItems
        }

        /**
         * @return offset for each item so active one would be arranged at viewport's center or near.
         */
        private fun calcOffsetShiftFor(activePosition: Int,
                                       positionFraction: Float): Float {
            if (allItems.size <= maxVisibleCount) {
                // Simply places center of ribbon into viewport center.
                return (viewportWidth / 2f) - (allItems.last().right / 2)
            }

            val viewportCenter = viewportWidth / 2f
            val activeItemOffset: Float
            var offset: Float
            if (view.isLayoutRtl()) {
                activeItemOffset = allItems[allItems.size - 1 - activePosition].centerOffset
                offset = viewportCenter - activeItemOffset + (spaceBetweenCenters * positionFraction)
            } else {
                activeItemOffset = allItems[activePosition].centerOffset
                offset = viewportCenter - activeItemOffset - (spaceBetweenCenters * positionFraction)
            }

            val centerIsBetweenItems = maxVisibleCount % 2 == 0
            if (centerIsBetweenItems) {
                offset += spaceBetweenCenters / 2
            }
            return offset
        }

        private fun downscaleAndDisperse(viewportItems: MutableList<Indicator>) {
            // Downscale items on sides when there are more items behind them.
            replaceAll(viewportItems) {
                val scaleFactor = calcScaleFraction(it.centerOffset)
                if (it.position == 0 || it.position == itemsCount - 1 || it.active) {
                    it.copy(scaleFactor = scaleFactor)
                } else {
                    scaleItem(it, scaleFactor)
                }
            }

            // Increase distance to downscaled items so viewport
            // won't show multiple downscaled bullets.
            val firstNonScaledIndex = viewportItems
                    .indexOfFirst { it.scaleFactor == 1f }
                    .takeIf { it >= 0 } ?: return
            val lastNonScaledIndex = viewportItems
                    .indexOfLast { it.scaleFactor == 1f }
                    .takeIf { it >= 0 } ?: return
            val leftDownscaledItemIndex = firstNonScaledIndex - 1
            val rightDownscaledItemIndex = lastNonScaledIndex + 1

            viewportItems.forEachIndexed { i, item ->
                if (i < leftDownscaledItemIndex) {
                    val scaleFactor = viewportItems.getOrNull(leftDownscaledItemIndex)?.scaleFactor
                            ?: return@forEachIndexed
                    val shiftFactor = 1f - scaleFactor
                    val shift = spaceBetweenCenters * shiftFactor
                    viewportItems[i] = item.copy(centerOffset = item.centerOffset - shift)
                }
                if (i > rightDownscaledItemIndex) {
                    val scaleFactor = viewportItems.getOrNull(rightDownscaledItemIndex)?.scaleFactor
                            ?: return@forEachIndexed
                    val shiftFactor = 1f - scaleFactor
                    val shift = spaceBetweenCenters * shiftFactor
                    viewportItems[i] = item.copy(centerOffset = item.centerOffset + shift)
                }
            }
        }

        private fun calcScaleFraction(absOffset: Float): Float {
            val minScaleOffset = 0f
            val maxScaleOffset = minScaleOffset + spaceBetweenCenters
            val itemOffset = if (absOffset <= maxScaleOffset) {
                absOffset
            } else {
                // Mirror offset from right corner to left.
                (viewportWidth - absOffset).coerceAtMost(maxScaleOffset)
            }

            if (itemOffset > maxScaleOffset) {
                return 1f
            }
            val fraction = itemOffset / (maxScaleOffset - minScaleOffset)
            return fraction.coerceIn(0f, 1f)
        }

        private fun scaleItem(item: Indicator, scaleFraction: Float): Indicator {
            val itemSize = item.itemSize
            val calculatedSize = itemSize.width * scaleFraction

            if (calculatedSize <= styleParams.minimumShape.itemSize.width) {
                return item.copy(
                    itemSize = styleParams.minimumShape.itemSize,
                    scaleFactor = scaleFraction)
            }

            if (calculatedSize < itemSize.width) {
                return when (itemSize) {
                    is IndicatorParams.ItemSize.RoundedRect -> {
                        item.copy(itemSize = itemSize.copy(
                            itemWidth = calculatedSize,
                            itemHeight = itemSize.itemHeight * (calculatedSize / itemSize.itemWidth),
                        ), scaleFactor = scaleFraction,)
                    }
                    is IndicatorParams.ItemSize.Circle -> {
                        item.copy(
                            itemSize = itemSize.copy(radius = (itemSize.width * scaleFraction) / 2f),
                            scaleFactor = scaleFraction,
                        )
                    }
                }
            }

            return item
        }

        private inline fun <T: Any> replaceAll(list: MutableList<T>, operator: (T) -> T) {
            list.forEachIndexed { i, element ->
                list[i] = operator(element)
            }
        }
    }
}
