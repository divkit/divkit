package com.yandex.div.core.view2.divs

import android.util.DisplayMetrics
import android.view.View
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivPagerIndicatorView
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.internal.widget.indicator.IndicatorParams
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivIndicatorItemPlacement
import com.yandex.div2.DivRoundedRectangleShape
import com.yandex.div2.DivShape
import javax.inject.Inject

internal class DivIndicatorBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
) : DivViewBinder<DivIndicator, DivPagerIndicatorView> {

    private val lateAttach = mutableListOf<(View) -> Unit>()

    override fun bindView(view: DivPagerIndicatorView, div: DivIndicator, divView: Div2View) {
        val oldDiv = view.div
        if (div == oldDiv) return

        val expressionResolver = divView.expressionResolver
        view.closeAllSubscription()

        view.div = div

        if (oldDiv != null) baseBinder.unbindExtensions(view, oldDiv, divView)
        baseBinder.bindView(view, div, oldDiv, divView)
        view.observeStyle(expressionResolver, div)
        lateAttach.add { rootView ->
            val pagerId = div.pagerId
            rootView.findViewWithTag<DivPagerView>(pagerId)?.let { view.attachPager(it.viewPager) }
        }
    }

    private fun DivPagerIndicatorView.observeStyle(resolver: ExpressionResolver, indicator: DivIndicator) {
        applyStyle(resolver, indicator)

        val callback = { _: Any -> applyStyle(resolver, indicator) }

        addSubscription(indicator.animation.observe(resolver, callback))

        if (indicator.activeShape == null || indicator.inactiveShape == null || indicator.inactiveMinimumShape == null) {
            addSubscription(indicator.activeItemColor.observe(resolver, callback))
            addSubscription(indicator.activeItemSize.observe(resolver, callback))
            addSubscription(indicator.inactiveItemColor.observe(resolver, callback))
            addSubscription(indicator.minimumItemSize.observe(resolver, callback))
            observeShape(resolver, indicator.shape, callback)
        } else {
            observeRoundedRectangleShape(resolver, indicator.activeShape!!, callback)
            observeRoundedRectangleShape(resolver, indicator.inactiveShape!!, callback)
            observeRoundedRectangleShape(resolver, indicator.inactiveMinimumShape!!, callback)
        }

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

        baseBinder.observeWidthAndHeightSubscription(resolver, this, indicator, callback)
    }

    private fun DivPagerIndicatorView.applyStyle(resolver: ExpressionResolver, indicator: DivIndicator) {
        val metrics = resources.displayMetrics
        val activeShape: IndicatorParams.Shape
        val inactiveShape: IndicatorParams.Shape
        val minimumShape: IndicatorParams.Shape
        if (indicator.activeShape != null && indicator.inactiveShape != null
            && indicator.inactiveMinimumShape != null) {
            activeShape = indicator.activeShape!!
                .toIndicatorParamsShape(metrics, resolver, indicator.activeItemColor)
            inactiveShape = indicator.inactiveShape!!
                .toIndicatorParamsShape(metrics, resolver, indicator.inactiveItemColor)
            minimumShape = indicator.inactiveMinimumShape!!
                .toIndicatorParamsShape(metrics, resolver, indicator.inactiveItemColor)
        } else {
            val activeColor = indicator.activeItemColor.evaluate(resolver)
            val inactiveColor = indicator.inactiveItemColor.evaluate(resolver)
            val activeItemSize = indicator.activeItemSize.evaluate(resolver).toFloat()
            val minimumItemSize = indicator.minimumItemSize.evaluate(resolver).toFloat()
            when (val shape = indicator.shape) {
                is DivShape.RoundedRectangle -> {
                    val width = shape.value.itemWidth.toPx(metrics, resolver)
                    val height = shape.value.itemHeight.toPx(metrics, resolver)
                    val cornerRadius = shape.value.cornerRadius.toPx(metrics, resolver)
                    activeShape = createRoundedRectangle(activeColor, width, height, cornerRadius, activeItemSize)
                    inactiveShape = createRoundedRectangle(inactiveColor, width, height, cornerRadius)
                    minimumShape = createRoundedRectangle(inactiveColor, width, height, cornerRadius, minimumItemSize)
                }
                is DivShape.Circle -> {
                    val radius = shape.value.radius.toPx(metrics, resolver)
                    activeShape = createCircle(activeColor, radius, activeItemSize)
                    inactiveShape = createCircle(inactiveColor, radius)
                    minimumShape = createCircle(inactiveColor, radius, minimumItemSize)
                }
            }
        }

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
                    maxVisibleItems = itemPlacement.value.maxVisibleItems.evaluate(resolver)
                )
            }
        )

        setStyle(style)
    }

    fun attachAll(view: View) {
        lateAttach.forEach{ it.invoke(view) }
        lateAttach.clear()
    }

    fun DivIndicator.Animation.convert(): IndicatorParams.Animation {
        if (this == DivIndicator.Animation.WORM) return IndicatorParams.Animation.WORM
        if (this == DivIndicator.Animation.SLIDER) return IndicatorParams.Animation.SLIDER
        return IndicatorParams.Animation.SCALE
    }

    private fun DivRoundedRectangleShape.toIndicatorParamsShape(
        metrics: DisplayMetrics,
        resolver: ExpressionResolver,
        deprecatedColor: Expression<Int>
    ) = createRoundedRectangle(
        color = (backgroundColor ?: deprecatedColor).evaluate(resolver),
        width = itemWidth.toPx(metrics, resolver),
        height = itemHeight.toPx(metrics, resolver),
        cornerRadius = cornerRadius.toPx(metrics, resolver)
    )
}
