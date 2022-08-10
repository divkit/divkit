package com.yandex.div.core.widget.indicator

import android.graphics.Canvas
import com.yandex.div.core.widget.indicator.animations.IndicatorAnimator
import com.yandex.div.core.widget.indicator.forms.SingleIndicatorDrawer

class IndicatorsStripDrawer(
    private val styleParams: IndicatorParams.Style,
    private var singleIndicatorDrawer: SingleIndicatorDrawer,
    private var animator: IndicatorAnimator,
) {
    private var itemsCount: Int = 0
    private var maxVisibleCount: Int = 0

    private var baseYOffset = styleParams.selectedWidth / 2f
    private var baseXOffset = styleParams.selectedWidth
    private var spaceBetweenCenters = styleParams.spaceBetweenCenters

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

            var itemWidth = animator.getItemWidthAt(index)
            var itemHeight = animator.getItemHeightAt(index)
            var cornerRadius = animator.getItemCornerRadiusAt(index)
            if (itemsCount > maxVisibleCount) {
                val scaleDistance = spaceBetweenCenters * 1.3f
                val smallScaleDistance = styleParams.selectedWidth / 2
                val currentScaleDistance = if (index == 0 || index == itemsCount - 1) {
                    smallScaleDistance
                } else {
                    scaleDistance
                }
                val viewportSize = viewportWidth
                if (xOffset < currentScaleDistance) { // left border
                    val calculatedSize = itemWidth * xOffset / currentScaleDistance
                    if (calculatedSize <= styleParams.minimumWidth) {
                        itemWidth = styleParams.minimumWidth
                        itemHeight = styleParams.minimumHeight
                        cornerRadius = styleParams.cornerRadius
                    } else if (calculatedSize < itemWidth) {
                        itemWidth = calculatedSize
                        itemHeight = itemHeight * xOffset / currentScaleDistance
                    }
                } else if (xOffset > viewportSize - currentScaleDistance) { // right border
                    val calculatedSize = itemWidth * (-xOffset + viewportSize) / currentScaleDistance
                    if (calculatedSize <= styleParams.minimumWidth) {
                        itemWidth = styleParams.minimumWidth
                        itemHeight = styleParams.minimumHeight
                        cornerRadius = styleParams.minimumCornerRadius
                    } else if (calculatedSize < itemWidth) {
                        itemWidth = calculatedSize
                        itemHeight = itemHeight * (-xOffset + viewportSize) / currentScaleDistance;
                    }
                }
            }

            singleIndicatorDrawer.draw(
                canvas,
                xOffset,
                baseYOffset,
                itemWidth,
                itemHeight,
                cornerRadius,
                animator.getColorAt(index)
            )
        }
        val xOffset = getItemOffsetAt(selectedItemPosition) - firstVisibleItemOffset
        val rect = animator.getSelectedItemRect(xOffset, baseYOffset)
        if (rect != null) {
            singleIndicatorDrawer.drawSelected(canvas, rect, styleParams.selectedCornerRadius)
        }
    }

    fun setItemsCount(count: Int) {
        itemsCount = count
        animator.setItemsCount(count)
        calculateMaximumVisibleItems()
        baseXOffset = (viewportWidth - spaceBetweenCenters * (maxVisibleCount - 1) ) / 2f
        baseYOffset = viewportHeight / 2f
    }

    fun calculateMaximumVisibleItems(viewportWidth: Int, viewportHeight: Int) {
        if (viewportWidth == 0 || viewportHeight == 0) return
        this.viewportWidth = viewportWidth
        this.viewportHeight = viewportHeight

        calculateMaximumVisibleItems()
        baseXOffset = (viewportWidth - spaceBetweenCenters * (maxVisibleCount - 1) ) / 2f
        baseYOffset = viewportHeight / 2f
        adjustVisibleItems(selectedItemPosition, selectedItemOffset)
    }

    fun getMaxVisibleItems() = maxVisibleCount

    private fun calculateMaximumVisibleItems() {
        maxVisibleCount = (((viewportWidth - styleParams.selectedWidth) / spaceBetweenCenters).toInt()).coerceAtMost(itemsCount)
    }

    private fun adjustVisibleItems(position: Int, positionOffset: Float) {
        if (itemsCount <= maxVisibleCount) {
            firstVisibleItemOffset = 0f
        } else {
            val minPage = maxVisibleCount / 2
            val maxPage = itemsCount - maxVisibleCount / 2 - 1

            firstVisibleItemOffset = if (itemsCount > maxVisibleCount) {
                when {
                    position < minPage -> getItemOffsetAt(minPage) - viewportWidth / 2
                    position >= maxPage -> getItemOffsetAt(maxPage) - viewportWidth / 2
                    else -> getItemOffsetAt(position) + spaceBetweenCenters * positionOffset - viewportWidth / 2
                }
            } else {
                0f
            }
        }

        startIndex = (((firstVisibleItemOffset - baseXOffset) / spaceBetweenCenters).toInt()).coerceAtLeast(0)
        endIndex = ((startIndex + viewportWidth / spaceBetweenCenters + 1).toInt()).coerceAtMost(itemsCount - 1)
    }

    private fun getItemOffsetAt(position: Int) = baseXOffset + spaceBetweenCenters * position

}
