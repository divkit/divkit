package com.yandex.div.core.view2.divs

import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivPagerIndicatorView
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.widget.indicator.IndicatorParams
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivShape
import javax.inject.Inject

internal class DivIndicatorBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
) : DivViewBinder<DivIndicator, DivPagerIndicatorView> {

    private val lateAttach = mutableListOf<() -> Unit>()

    override fun bindView(view: DivPagerIndicatorView, div: DivIndicator, divView: Div2View) {
        val oldDiv = view.div
        if (div == oldDiv) return

        val expressionResolver = divView.expressionResolver
        view.closeAllSubscription()

        view.div = div

        if (oldDiv != null) baseBinder.unbindExtensions(view, oldDiv, divView)
        baseBinder.bindView(view, div, oldDiv, divView)
        view.observeStyle(expressionResolver, div)
        lateAttach.add {
            val pagerId = div.pagerId
            divView.findViewWithTag<DivPagerView>(pagerId)?.let { view.attachPager(it.viewPager) }
        }
    }

    private fun DivPagerIndicatorView.observeStyle(resolver: ExpressionResolver, indicator: DivIndicator) {
        applyStyle(resolver, indicator)

        val callback = { _: Any -> applyStyle(resolver, indicator) }
        addSubscription(indicator.activeItemColor.observe(resolver, callback))
        addSubscription(indicator.activeItemSize.observe(resolver, callback))
        addSubscription(indicator.inactiveItemColor.observe(resolver, callback))
        addSubscription(indicator.minimumItemSize.observe(resolver, callback))
        addSubscription(indicator.spaceBetweenCenters.value.observe(resolver, callback))
        addSubscription(indicator.spaceBetweenCenters.unit.observe(resolver, callback))
        addSubscription(indicator.animation.observe(resolver, callback))

        observeShape(resolver, indicator.shape, callback)

        baseBinder.observeWidthAndHeightSubscription(resolver, this, indicator, callback)
    }

    private fun DivPagerIndicatorView.applyStyle(resolver: ExpressionResolver, indicator: DivIndicator) {
        val metrics = resources.displayMetrics
        val style = IndicatorParams.Style(
            color = indicator.inactiveItemColor.evaluate(resolver),
            selectedColor = indicator.activeItemColor.evaluate(resolver),
            spaceBetweenCenters = indicator.spaceBetweenCenters.toPx(metrics, resolver).toFloat(),
            animation = indicator.animation.evaluate(resolver).convert(),
            shape = when (val shape = indicator.shape) {
                is DivShape.RoundedRectangle -> IndicatorParams.Shape.RoundedRect(
                    normalWidth = shape.value.itemWidth.toPx(metrics, resolver).toFloat(),
                    normalHeight = shape.value.itemHeight.toPx(metrics, resolver).toFloat(),
                    selectedWidth = shape.value.itemWidth.toPx(metrics, resolver) * indicator.activeItemSize.evaluate(resolver).toFloat(),
                    selectedHeight = shape.value.itemHeight.toPx(metrics, resolver) * indicator.activeItemSize.evaluate(resolver).toFloat(),
                    minimumWidth = shape.value.itemWidth.toPx(metrics, resolver) * indicator.minimumItemSize.evaluate(resolver).toFloat(),
                    minimumHeight = shape.value.itemHeight.toPx(metrics, resolver) * indicator.minimumItemSize.evaluate(resolver).toFloat(),
                    cornerRadius = shape.value.cornerRadius.toPx(metrics, resolver).toFloat(),
                    selectedCornerRadius = shape.value.cornerRadius.toPx(metrics, resolver) * indicator.activeItemSize.evaluate(resolver).toFloat(),
                    minimumCornerRadius = shape.value.cornerRadius.toPx(metrics, resolver) * indicator.minimumItemSize.evaluate(resolver).toFloat(),
                )
                is DivShape.Circle -> IndicatorParams.Shape.Circle(
                    normalRadius = shape.value.radius.toPx(metrics, resolver).toFloat(),
                    selectedRadius = shape.value.radius.toPx(metrics, resolver) * indicator.activeItemSize.evaluate(resolver).toFloat(),
                    minimumRadius = shape.value.radius.toPx(metrics, resolver) * indicator.minimumItemSize.evaluate(resolver).toFloat(),
                )
            },
        )
        setStyle(style)
    }

    fun attachAll() {
        lateAttach.forEach{ it.invoke() }
        lateAttach.clear()
    }

    fun DivIndicator.Animation.convert(): IndicatorParams.Animation {
        if (this == DivIndicator.Animation.WORM) return IndicatorParams.Animation.WORM
        if (this == DivIndicator.Animation.SLIDER) return IndicatorParams.Animation.SLIDER
        return IndicatorParams.Animation.SCALE
    }
}
