package com.yandex.div.core.view2.divs

import android.util.DisplayMetrics
import com.yandex.div.core.util.observeFixedSize
import com.yandex.div.core.util.observeRoundedRectangleShape
import com.yandex.div.core.util.observeShape
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivPagerIndicatorView
import com.yandex.div.internal.widget.indicator.IndicatorParams
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivIndicatorItemPlacement
import com.yandex.div2.DivRoundedRectangleShape
import com.yandex.div2.DivShape
import com.yandex.div2.DivSizeUnit
import javax.inject.Inject

internal class DivIndicatorBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val pagerIndicatorConnector: PagerIndicatorConnector
) : DivViewBinder<DivIndicator, DivPagerIndicatorView> {
    override fun bindView(view: DivPagerIndicatorView, div: DivIndicator, divView: Div2View) {
        div.pagerId?.let {
            pagerIndicatorConnector.submitIndicator(it, view)
        }

        val oldDiv = view.div
        if (div === oldDiv) return

        val expressionResolver = divView.expressionResolver

        baseBinder.bindView(view, div, oldDiv, divView)
        view.observeStyle(expressionResolver, div)
    }

    private fun DivPagerIndicatorView.observeStyle(resolver: ExpressionResolver, indicator: DivIndicator) {
        applyStyle(resolver, indicator)

        val callback = { _: Any -> applyStyle(resolver, indicator) }

        addSubscription(indicator.animation.observe(resolver, callback))

        addSubscription(indicator.activeItemColor.observe(resolver, callback))
        addSubscription(indicator.activeItemSize.observe(resolver, callback))
        addSubscription(indicator.inactiveItemColor.observe(resolver, callback))
        addSubscription(indicator.minimumItemSize.observe(resolver, callback))
        observeShape(indicator.shape, resolver, callback)
        observeRoundedRectangleShape(indicator.activeShape, resolver, callback)
        observeRoundedRectangleShape(indicator.inactiveShape, resolver, callback)
        observeRoundedRectangleShape(indicator.inactiveMinimumShape, resolver, callback)

        when(val itemsPlacement = indicator.itemsPlacementCompat) {
            is DivIndicatorItemPlacement.Default -> {
                addSubscription(itemsPlacement.value.spaceBetweenCenters.value.observe(resolver, callback))
                addSubscription(itemsPlacement.value.spaceBetweenCenters.unit.observe(resolver, callback))
            }
            is DivIndicatorItemPlacement.Stretch -> {
                addSubscription(itemsPlacement.value.itemSpacing.value.observe(resolver, callback))
                addSubscription(itemsPlacement.value.itemSpacing.unit.observe(resolver, callback))
                addSubscription(itemsPlacement.value.maxVisibleItems.observe(resolver, callback))
            }
        }

        observeWidthAndHeightSubscription(indicator, resolver, callback)
    }

    private fun DivPagerIndicatorView.observeWidthAndHeightSubscription(
        div: DivBase,
        resolver: ExpressionResolver,
        callback: (Any) -> Unit
    ) {
        val width = div.width.value()
        if (width is DivFixedSize) {
            observeFixedSize(width, resolver, callback)
        }

        val height = div.height.value()
        if (height is DivFixedSize) {
            observeFixedSize(height, resolver, callback)
        }
    }

    private fun DivPagerIndicatorView.applyStyle(resolver: ExpressionResolver, indicator: DivIndicator) {
        val metrics = resources.displayMetrics
        val activeIndicatorShape = indicator.activeShape
        val inactiveIndicatorShape = indicator.inactiveShape
        val minimumIndicatorShape = indicator.inactiveMinimumShape
        val activeItemSize = indicator.activeItemSize.evaluate(resolver).toFloat()
        val minimumItemSize = indicator.minimumItemSize.evaluate(resolver).toFloat()
        val inactiveShape = inactiveIndicatorShape?.toIndicatorParamsShape(metrics,
                resolver, indicator.inactiveItemColor)
            ?: activeIndicatorShape?.toIndicatorParamsShape(metrics,
                resolver, indicator.inactiveItemColor, 1/activeItemSize)
            ?: minimumIndicatorShape?.toIndicatorParamsShape(metrics,
                resolver, indicator.inactiveItemColor, minimumItemSize)
            ?: indicator.shape.toIndicatorParamsShape(metrics, resolver, indicator.inactiveItemColor)
        val activeShape = activeIndicatorShape?.toIndicatorParamsShape(metrics, resolver, indicator.activeItemColor)
            ?: inactiveShape.multiply(activeItemSize, indicator.activeItemColor.evaluate(resolver))
        val minimumShape = minimumIndicatorShape?.toIndicatorParamsShape(metrics, resolver, indicator.inactiveItemColor)
            ?: inactiveShape.multiply(minimumItemSize)

        val style = IndicatorParams.Style(
            animation = indicator.animation.evaluate(resolver).convert(),
            activeShape = activeShape,
            inactiveShape = inactiveShape,
            minimumShape = minimumShape,
            itemsPlacement = when(val itemPlacement = indicator.itemsPlacementCompat) {
                is DivIndicatorItemPlacement.Default -> IndicatorParams.ItemPlacement.Default(
                    spaceBetweenCenters = itemPlacement.value.spaceBetweenCenters.toPx(metrics, resolver).toFloat()
                )
                is DivIndicatorItemPlacement.Stretch -> IndicatorParams.ItemPlacement.Stretch(
                    itemSpacing = itemPlacement.value.itemSpacing.toPx(metrics, resolver).toFloat(),
                    maxVisibleItems = itemPlacement.value.maxVisibleItems.evaluate(resolver).toIntSafely()
                )
            }
        )

        setStyle(style)
    }

    fun DivIndicator.Animation.convert(): IndicatorParams.Animation {
        if (this == DivIndicator.Animation.WORM) return IndicatorParams.Animation.WORM
        if (this == DivIndicator.Animation.SLIDER) return IndicatorParams.Animation.SLIDER
        return IndicatorParams.Animation.SCALE
    }

    private fun DivRoundedRectangleShape.toIndicatorParamsShape(
        metrics: DisplayMetrics,
        resolver: ExpressionResolver,
        deprecatedColor: Expression<Int>,
        multiplier: Float = 1f
    ): IndicatorParams.Shape {
        val borderUnit = stroke?.unit?.evaluate(resolver) ?: DivSizeUnit.DP
        val borderWidth = stroke?.width?.evaluate(resolver)?.unitToPx(metrics, borderUnit)
        return createRoundedRectangle(
            color = (backgroundColor ?: deprecatedColor).evaluate(resolver),
            width = itemWidth.toPxF(metrics, resolver),
            height = itemHeight.toPxF(metrics, resolver),
            cornerRadius = cornerRadius.toPxF(metrics, resolver),
            strokeWidth = borderWidth?.toFloat(),
            strokeColor = stroke?.color?.evaluate(resolver),
            multiplier = multiplier
        )
    }

    private fun DivShape.toIndicatorParamsShape(
        metrics: DisplayMetrics,
        resolver: ExpressionResolver,
        color: Expression<Int>,
        multiplier: Float = 1f
    ): IndicatorParams.Shape {
        return when (val shape = this) {
            is DivShape.RoundedRectangle -> {
                shape.value.toIndicatorParamsShape(metrics, resolver, color, multiplier)
            }
            is DivShape.Circle -> {
                val radius = shape.value.radius.toPxF(metrics, resolver)
                createCircle(color.evaluate(resolver), radius, multiplier)
            }
        }
    }

    private fun IndicatorParams.Shape.multiply(multiplier: Float, color: Int? = null): IndicatorParams.Shape {
        when (val shape = this) {
            is IndicatorParams.Shape.RoundedRect -> {
                return createRoundedRectangle(
                    color = color ?: shape.color,
                    width = shape.itemSize.itemWidth,
                    height = shape.itemSize.itemHeight,
                    cornerRadius = shape.itemSize.cornerRadius,
                    multiplier = multiplier,
                    strokeWidth = shape.strokeWidth,
                    strokeColor = shape.strokeColor
                )
            }
            is IndicatorParams.Shape.Circle -> {
                return createCircle(
                    color = color ?: shape.color,
                    radius = shape.itemSize.radius,
                    multiplier = multiplier
                )
            }
        }
    }
}
