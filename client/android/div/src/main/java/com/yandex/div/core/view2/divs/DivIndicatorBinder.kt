package com.yandex.div.core.view2.divs

import android.graphics.Color
import android.util.DisplayMetrics
import com.yandex.div.core.util.findNearest
import com.yandex.div.core.util.observeFixedSize
import com.yandex.div.core.util.observeRoundedRectangleShape
import com.yandex.div.core.util.observeShape
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.pager.PagerIndicatorConnector
import com.yandex.div.core.view2.divs.widgets.DivPagerIndicatorView
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.widget.indicator.IndicatorParams
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivDefaultIndicatorItemPlacement
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivIndicatorItemPlacement
import com.yandex.div2.DivPager
import com.yandex.div2.DivRoundedRectangleShape
import com.yandex.div2.DivShape
import com.yandex.div2.DivSizeUnit
import javax.inject.Inject

internal class DivIndicatorBinder @Inject constructor(
    baseBinder: DivBaseBinder,
    private val pagerIndicatorConnector: PagerIndicatorConnector
) : DivViewBinder<DivBlock.Indicator, DivPagerIndicatorView>(baseBinder) {

    override fun bindView(view: DivPagerIndicatorView, divBlock: DivBlock.Indicator, divView: Div2View) {
        val div = divBlock.divValue
        val rootDiv = divView.rootDiv()
            ?: return super.bindView(view, divBlock, divView)

        val rootPath = divView.currentRootPath
        val rootDivResolver = divView.runtimeStore
            .getOrCreateRuntime(rootPath.fullPath, rootDiv, divView.expressionResolver)
            .expressionResolver
        findNearest<DivPager>(rootDiv, rootDivResolver, rootPath, div) {
            div.pagerId == null || it.id == div.pagerId
        }?.let { pagerToAttach ->
            pagerIndicatorConnector.submitIndicator(view, pagerToAttach)
        }
        super.bindView(view, divBlock, divView)
    }

    override fun DivPagerIndicatorView.bind(
        divBlock: DivBlock.Indicator,
        oldDivBlock: DivBlock.Indicator?,
        divView: Div2View,
    ) {
        val div = divBlock.divValue
        val expressionResolver = divBlock.expressionResolver
        applyStyle(expressionResolver, div)

        val callback = { _: Any -> applyStyle(expressionResolver, div) }

        addSubscription(div.animation.observe(expressionResolver, callback))

        addSubscription(div.activeItemColor.observe(expressionResolver, callback))
        addSubscription(div.activeItemSize.observe(expressionResolver, callback))
        addSubscription(div.inactiveItemColor.observe(expressionResolver, callback))
        addSubscription(div.minimumItemSize.observe(expressionResolver, callback))
        observeShape(div.shape, expressionResolver, callback)
        observeRoundedRectangleShape(div.activeShape, expressionResolver, callback)
        observeRoundedRectangleShape(div.inactiveShape, expressionResolver, callback)
        observeRoundedRectangleShape(div.inactiveMinimumShape, expressionResolver, callback)

        when(val itemsPlacement = div.itemsPlacementCompat) {
            is DivIndicatorItemPlacement.Default -> {
                addSubscription(itemsPlacement.value.spaceBetweenCenters.value.observe(expressionResolver, callback))
                addSubscription(itemsPlacement.value.spaceBetweenCenters.unit.observe(expressionResolver, callback))
            }
            is DivIndicatorItemPlacement.Stretch -> {
                addSubscription(itemsPlacement.value.itemSpacing.value.observe(expressionResolver, callback))
                addSubscription(itemsPlacement.value.itemSpacing.unit.observe(expressionResolver, callback))
                addSubscription(itemsPlacement.value.maxVisibleItems.observe(expressionResolver, callback))
            }
        }

        observeWidthAndHeightSubscription(div, expressionResolver, callback)
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

    private val DivIndicator.itemsPlacementCompat get() =
        itemsPlacement ?: DivIndicatorItemPlacement.Default(DivDefaultIndicatorItemPlacement(spaceBetweenCenters))

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

    companion object {

        internal fun createRoundedRectangle(
            color: Int,
            width: Float,
            height: Float,
            cornerRadius: Float,
            multiplier: Float = 1f,
            strokeWidth: Float? = null,
            strokeColor: Int? = null
        ): IndicatorParams.Shape =
            IndicatorParams.Shape.RoundedRect(
                color = color,
                itemSize = IndicatorParams.ItemSize.RoundedRect(
                    itemWidth = width * multiplier,
                    itemHeight = height * multiplier,
                    cornerRadius = cornerRadius * multiplier
                ),
                strokeWidth = strokeWidth ?: 0f,
                strokeColor = strokeColor ?: Color.TRANSPARENT
            )

        internal fun createCircle(
            color: Int,
            radius: Float,
            multiplier: Float = 1f
        ): IndicatorParams.Shape =
            IndicatorParams.Shape.Circle(
                color = color,
                itemSize = IndicatorParams.ItemSize.Circle(
                    radius = radius * multiplier
                )
            )
    }
}
