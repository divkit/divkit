package com.yandex.div.internal.widget.indicator

import android.graphics.Canvas
import com.yandex.div.internal.widget.indicator.animations.IndicatorAnimator
import com.yandex.div.internal.widget.indicator.forms.SingleIndicatorDrawer

internal class IndicatorsStripDrawer(
    private val styleParams: IndicatorParams.Style,
    private val singleIndicatorDrawer: SingleIndicatorDrawer,
    private val animator: IndicatorAnimator,
) {
    private var itemsCount: Int = 0
    private var maxVisibleCount: Int = 0

    private var baseYOffset: Float = styleParams.inactiveShape.itemSize.width
    private var baseXOffset: Float = styleParams.inactiveShape.itemSize.width / 2
    private var spaceBetweenCenters: Float = 0f
    private var itemWidthMultiplier: Float = 1f

    private var viewportWidth: Int = 0
    private var viewportHeight: Int = 0
    private var selectedItemPosition: Int = 0
    private var selectedItemOffset: Float = 0f
    private var firstVisibleItemOffset: Float = 0f
    private var startIndex: Int = 0
    private var endIndex: Int = maxVisibleCount - 1

    fun onPageScrolled(position: Int, positionOffset: Float) {
        selectedItemPosition = position
        selectedItemOffset = positionOffset
        animator.onPageScrolled(position, positionOffset)
        adjustVisibleItems(position, positionOffset)
    }

    fun onPageSelected(position: Int) {
        selectedItemPosition = position
        selectedItemOffset = 0f
        animator.onPageSelected(position)
        adjustVisibleItems(position, 0f)
    }

    fun onDraw(canvas: Canvas) {
        for (index in startIndex..endIndex) {
            val xOffset = getItemOffsetAt(index) - firstVisibleItemOffset

            if (xOffset !in 0f..viewportWidth.toFloat()) continue
            var itemSize = getItemSizeAt(index)
            if (itemsCount > maxVisibleCount) {
                val scaleDistance = spaceBetweenCenters * 1.3f
                val smallScaleDistance = styleParams.inactiveShape.itemSize.width / 2
                val currentScaleDistance = if (index == 0 || index == itemsCount - 1) {
                    smallScaleDistance
                } else {
                    scaleDistance
                }
                val viewportSize = viewportWidth
                if (xOffset < currentScaleDistance) { // left border
                    val calculatedSize = itemSize.width * xOffset / currentScaleDistance
                    if (calculatedSize <= styleParams.minimumShape.itemSize.width) {
                        itemSize = styleParams.minimumShape.itemSize
                    } else if (calculatedSize < itemSize.width) {
                        when (itemSize) {
                            is IndicatorParams.ItemSize.RoundedRect -> {
                                itemSize.itemWidth = calculatedSize
                                itemSize.itemHeight = itemSize.itemHeight * xOffset / currentScaleDistance
                            }
                            is IndicatorParams.ItemSize.Circle -> {
                                itemSize.radius = calculatedSize
                            }
                        }
                    }
                } else if (xOffset > viewportSize - currentScaleDistance) { // right border
                    val calculatedSize = itemSize.width * (-xOffset + viewportSize) / currentScaleDistance
                    if (calculatedSize <= styleParams.minimumShape.itemSize.width) {
                        itemSize = styleParams.minimumShape.itemSize
                    } else if (calculatedSize < itemSize.width) {
                        when (itemSize) {
                            is IndicatorParams.ItemSize.RoundedRect -> {
                                itemSize.itemWidth = calculatedSize
                                itemSize.itemHeight = itemSize.itemHeight * (-xOffset + viewportSize) / currentScaleDistance
                            }
                            is IndicatorParams.ItemSize.Circle -> {
                                itemSize.radius = calculatedSize
                            }
                        }
                    }
                }
            }

            singleIndicatorDrawer.draw(
                canvas,
                xOffset,
                baseYOffset,
                itemSize,
                animator.getColorAt(index),
                animator.getBorderWidthAt(index),
                animator.getBorderColorAt(index)
            )
        }
        val xOffset = getItemOffsetAt(selectedItemPosition) - firstVisibleItemOffset
        val rect = animator.getSelectedItemRect(xOffset, baseYOffset)
        if (rect != null) {
            singleIndicatorDrawer.drawSelected(canvas, rect)
        }
    }

    fun setItemsCount(count: Int) {
        itemsCount = count
        animator.setItemsCount(count)

        calculateMaximumVisibleItems()
        baseXOffset = (viewportWidth - spaceBetweenCenters * (maxVisibleCount - 1)) / 2f
        baseYOffset = viewportHeight / 2f
    }

    fun calculateMaximumVisibleItems(viewportWidth: Int, viewportHeight: Int) {
        if (viewportWidth == 0 || viewportHeight == 0) return
        this.viewportWidth = viewportWidth
        this.viewportHeight = viewportHeight

        calculateMaximumVisibleItems()
        adjustItemsPlacement()

        baseXOffset = (viewportWidth - spaceBetweenCenters * (maxVisibleCount - 1)) / 2f
        baseYOffset = viewportHeight / 2f
        adjustVisibleItems(selectedItemPosition, selectedItemOffset)
    }

    fun getMaxVisibleItems() = maxVisibleCount

    private fun calculateMaximumVisibleItems() {
        maxVisibleCount = when (val itemPlacement = styleParams.itemsPlacement) {
            is IndicatorParams.ItemPlacement.Default -> (((viewportWidth - styleParams.activeShape.itemSize.width) /
                    itemPlacement.spaceBetweenCenters).toInt())
            is IndicatorParams.ItemPlacement.Stretch -> itemPlacement.maxVisibleItems
        }.coerceAtMost(itemsCount)
    }

    private fun adjustVisibleItems(position: Int, positionOffset: Float) {
        if (itemsCount <= maxVisibleCount) {
            firstVisibleItemOffset = 0f
        } else {
            val minPage = maxVisibleCount / 2
            val maxPage = itemsCount - maxVisibleCount / 2 - maxVisibleCount % 2
            val centerOffset = if (maxVisibleCount % 2 == 0) spaceBetweenCenters / 2 else 0f

            firstVisibleItemOffset = if (itemsCount > maxVisibleCount) {
                when {
                    position < minPage -> getItemOffsetAt(minPage) - viewportWidth / 2 - centerOffset
                    position >= maxPage -> getItemOffsetAt(maxPage) - viewportWidth / 2 - centerOffset
                    else -> getItemOffsetAt(position) + spaceBetweenCenters * positionOffset - viewportWidth / 2 - centerOffset
                }
            } else {
                0f
            }
        }

        startIndex = (((firstVisibleItemOffset - baseXOffset) / spaceBetweenCenters).toInt()).coerceAtLeast(0)
        endIndex = ((startIndex + viewportWidth / spaceBetweenCenters + 1).toInt()).coerceAtMost(itemsCount - 1)
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

    private fun getItemOffsetAt(position: Int) = baseXOffset + spaceBetweenCenters * position

    private fun getItemSizeAt(position: Int): IndicatorParams.ItemSize {
        var itemSize = animator.getItemSizeAt(position)

        if (itemWidthMultiplier != 1.0f && itemSize is IndicatorParams.ItemSize.RoundedRect) {
            itemSize = itemSize.copy(itemWidth = itemSize.itemWidth * itemWidthMultiplier)
            animator.overrideItemWidth(itemSize.itemWidth)
        }

        return itemSize
    }
}
