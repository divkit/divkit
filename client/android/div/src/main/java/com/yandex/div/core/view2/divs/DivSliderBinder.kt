package com.yandex.div.core.view2.divs

import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import androidx.core.view.doOnPreDraw
import com.yandex.div.core.Disposable
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment
import com.yandex.div.core.expression.variables.TwoWayIntegerVariableBinder
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivSliderView
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.font.DivTypefaceProvider
import com.yandex.div.core.widget.slider.SliderTextStyle
import com.yandex.div.core.widget.slider.SliderView
import com.yandex.div.core.widget.slider.shapes.*
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivShape
import com.yandex.div2.DivShapeDrawable
import com.yandex.div2.DivSlider
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.roundToInt

private const val SLIDER_TICKS_OVERLAP_WARNING = "Slider ticks overlap each other."

internal class DivSliderBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val logger: Div2Logger,
    private val typefaceProvider: DivTypefaceProvider,
    private val variableBinder: TwoWayIntegerVariableBinder,
    private val errorCollectors: ErrorCollectors,
    @ExperimentFlag(Experiment.VISUAL_ERRORS_ENABLED) private val visualErrorsEnabled: Boolean,
) : DivViewBinder<DivSlider, DivSliderView> {

    private var errorCollector: ErrorCollector? = null

    override fun bindView(view: DivSliderView, div: DivSlider, divView: Div2View) {
        val oldDiv = view.div
        errorCollector = errorCollectors.getOrCreate(divView.dataTag, divView.divData)
        if (div == oldDiv) return

        val expressionResolver = divView.expressionResolver
        view.closeAllSubscription()

        view.div = div
        if (oldDiv != null) baseBinder.unbindExtensions(view, oldDiv, divView)
        baseBinder.bindView(view, div, oldDiv, divView)

        view.addSubscription(
            div.minValue.observeAndGet(expressionResolver) { minValue ->
                view.minValue = minValue.toFloat()
                view.checkSliderTicks()
            }
        )
        view.addSubscription(
            div.maxValue.observeAndGet(expressionResolver) { maxValue ->
                view.maxValue = maxValue.toFloat()
                view.checkSliderTicks()
            }
        )

        view.clearOnThumbChangedListener()
        view.setupThumb(div, divView, expressionResolver)
        view.setupSecondaryThumb(div, divView, expressionResolver)

        view.setupTrack(div, expressionResolver)
        view.setupTickMarks(div, expressionResolver)
    }

    private fun DivSliderView.setupThumb(
        div: DivSlider,
        divView: Div2View,
        resolver: ExpressionResolver
    ) {
        observeThumbValue(div, divView)
        observeThumbStyle(resolver, div.thumbStyle)
        observeThumbTextStyle(resolver, div.thumbTextStyle)
    }

    private fun DivSliderView.observeThumbValue(div: DivSlider, divView: Div2View) {
        val variableName = div.thumbValueVariable ?: return
        val callbacks = object : TwoWayIntegerVariableBinder.Callbacks {
            override fun onVariableChanged(value: Int?) {
                setThumbValue(value?.toFloat() ?: 0f, false)
            }

            override fun setViewStateChangeListener(valueUpdater: (Int) -> Unit) {
                addOnThumbChangedListener(object : SliderView.ChangedListener {
                    override fun onThumbValueChanged(value: Float) {
                        logger.logSliderDrag(divView, this@observeThumbValue, value)
                        valueUpdater(value.roundToInt())
                    }
                })
            }
        }

        addSubscription(variableBinder.bindVariable(divView, variableName, callbacks))
    }

    private fun DivSliderView.observeThumbStyle(resolver: ExpressionResolver, thumbStyle: DivDrawable) {
        observeDrawable(resolver, thumbStyle) { style ->
            applyThumbStyle(resolver, style)
        }
    }

    private fun SliderView.applyThumbStyle(resolver: ExpressionResolver, thumbStyle: DivDrawable) {
        thumbDrawable = thumbStyle.toDrawable(resources.displayMetrics, resolver)
    }

    private fun DivSliderView.observeThumbSecondaryStyle(resolver: ExpressionResolver, thumbStyle: DivDrawable) {
        observeDrawable(resolver, thumbStyle) { style ->
            applyThumbSecondaryStyle(resolver, style)
        }
    }

    private fun SliderView.applyThumbSecondaryStyle(resolver: ExpressionResolver, thumbStyle: DivDrawable) {
        thumbSecondaryDrawable = thumbStyle.toDrawable(resources.displayMetrics, resolver)
    }

    private fun DivSliderView.observeThumbTextStyle(
        resolver: ExpressionResolver,
        thumbTextStyle: DivSlider.TextStyle?
    ) {
        applyThumbTextStyle(resolver, thumbTextStyle)

        if (thumbTextStyle == null) {
            return
        }

        addSubscription(thumbTextStyle.textColor.observe(resolver) { applyThumbTextStyle(resolver, thumbTextStyle) })
    }

    private fun SliderView.applyThumbTextStyle(
        resolver: ExpressionResolver,
        textStyle: DivSlider.TextStyle?
    ) {
        thumbTextDrawable = textStyle?.let {
            TextDrawable(it.toSliderTextStyle(resources.displayMetrics, typefaceProvider, resolver))
        }
    }

    private fun DivSliderView.observeThumbSecondaryTextStyle(
        resolver: ExpressionResolver,
        thumbTextStyle: DivSlider.TextStyle?
    ) {
        applyThumbSecondaryTextStyle(resolver, thumbTextStyle)

        if (thumbTextStyle == null) {
            return
        }

        addSubscription(
            thumbTextStyle.textColor.observe(resolver) { applyThumbSecondaryTextStyle(resolver, thumbTextStyle) }
        )
    }

    private fun SliderView.applyThumbSecondaryTextStyle(
        resolver: ExpressionResolver,
        textStyle: DivSlider.TextStyle?
    ) {
        thumbSecondTextDrawable = textStyle?.let {
            TextDrawable(it.toSliderTextStyle(resources.displayMetrics, typefaceProvider, resolver))
        }
    }

    private fun DivSliderView.setupSecondaryThumb(
        div: DivSlider,
        divView: Div2View,
        resolver: ExpressionResolver
    ) {
        val variableName = div.thumbSecondaryValueVariable
        variableName ?: run {
            thumbSecondaryDrawable = null
            setThumbSecondaryValue(null, false)
            return
        }

        observeThumbSecondaryValue(variableName, divView)
        div.thumbSecondaryStyle?.let { observeThumbSecondaryStyle(resolver, it) }
            ?: observeThumbSecondaryStyle(resolver, div.thumbStyle)
        observeThumbSecondaryTextStyle(resolver, div.thumbSecondaryTextStyle)
    }

    private fun DivSliderView.observeThumbSecondaryValue(variableName: String, divView: Div2View) {
        val callbacks = object : TwoWayIntegerVariableBinder.Callbacks {
            override fun onVariableChanged(value: Int?) {
                setThumbSecondaryValue(value?.toFloat(), false)
            }

            override fun setViewStateChangeListener(valueUpdater: (Int) -> Unit) {
                addOnThumbChangedListener(object : SliderView.ChangedListener {
                    override fun onThumbSecondaryValueChanged(value: Float?) {
                        logger.logSliderDrag(divView, this@observeThumbSecondaryValue, value)
                        valueUpdater(value?.roundToInt() ?: 0)
                    }
                })
            }
        }

        addSubscription(variableBinder.bindVariable(divView, variableName, callbacks))
    }

    private fun DivSliderView.setupTrack(div: DivSlider, resolver: ExpressionResolver) {
        observeTrackActiveStyle(resolver, div.trackActiveStyle)
        observeTrackInactiveStyle(resolver, div.trackInactiveStyle)
    }

    private fun DivSliderView.observeTrackActiveStyle(resolver: ExpressionResolver, trackStyle: DivDrawable) {
        observeDrawable(resolver, trackStyle) { style ->
            applyTrackActiveStyle(resolver, style)
        }
    }

    private fun SliderView.applyTrackActiveStyle(resolver: ExpressionResolver, trackStyle: DivDrawable) {
        activeTrackDrawable = trackStyle.toDrawable(resources.displayMetrics, resolver)
    }

    private fun DivSliderView.observeTrackInactiveStyle(resolver: ExpressionResolver, trackStyle: DivDrawable) {
        observeDrawable(resolver, trackStyle) { style ->
            applyTrackInactiveStyle(resolver, style)
        }
    }

    private fun SliderView.applyTrackInactiveStyle(resolver: ExpressionResolver, trackStyle: DivDrawable) {
        inactiveTrackDrawable = trackStyle.toDrawable(resources.displayMetrics, resolver)
    }

    private fun DivSliderView.setupTickMarks(div: DivSlider, resolver: ExpressionResolver) {
        observeTickMarkActiveStyle(resolver, div.tickMarkActiveStyle)
        observeTickMarkInactiveStyle(resolver, div.tickMarkInactiveStyle)
    }

    private fun DivSliderView.observeTickMarkActiveStyle(resolver: ExpressionResolver, tickMarkStyle: DivDrawable?) {
        tickMarkStyle?.let {
            observeDrawable(resolver, it) { style ->
                applyTickMarkActiveStyle(resolver, style)
            }
        }
    }

    private fun DivSliderView.applyTickMarkActiveStyle(resolver: ExpressionResolver, tickMarkStyle: DivDrawable?) {
        activeTickMarkDrawable = tickMarkStyle?.toDrawable(resources.displayMetrics, resolver)
        checkSliderTicks()
    }

    private fun DivSliderView.observeTickMarkInactiveStyle(resolver: ExpressionResolver, tickMarkStyle: DivDrawable?) {
        tickMarkStyle?.let {
            observeDrawable(resolver, tickMarkStyle) { style ->
                applyTickMarkInactiveStyle(resolver, style)
            }
        }
    }

    private fun DivSliderView.applyTickMarkInactiveStyle(resolver: ExpressionResolver, tickMarkStyle: DivDrawable?) {
        inactiveTickMarkDrawable = tickMarkStyle?.toDrawable(resources.displayMetrics, resolver)
        checkSliderTicks()
    }

    private fun DivSliderView.observeDrawable(
        resolver: ExpressionResolver,
        drawable: DivDrawable,
        applyDrawable: (DivDrawable) -> Unit
    ) {
        applyDrawable(drawable)

        val callback = { _: Any -> applyDrawable(drawable) }
        when (drawable) {
            is DivDrawable.Shape -> {
                val shapeDrawable = drawable.value
                val roundedRect = shapeDrawable.shape as? DivShape.RoundedRectangle
                addSubscription(shapeDrawable.color.observe(resolver, callback))
                addSubscription(shapeDrawable.stroke?.color?.observe(resolver, callback)
                    ?: Disposable.NULL)
                addSubscription(shapeDrawable.stroke?.width?.observe(resolver, callback)
                    ?: Disposable.NULL)
                addSubscription(roundedRect?.value?.itemWidth?.value?.observe(resolver, callback)
                    ?: Disposable.NULL)
                addSubscription(roundedRect?.value?.itemWidth?.unit?.observe(resolver, callback)
                    ?: Disposable.NULL)
                addSubscription(roundedRect?.value?.itemHeight?.value?.observe(resolver, callback)
                    ?: Disposable.NULL)
                addSubscription(roundedRect?.value?.itemHeight?.unit?.observe(resolver, callback)
                    ?: Disposable.NULL)
                addSubscription(roundedRect?.value?.cornerRadius?.value?.observe(resolver, callback)
                    ?: Disposable.NULL)
                addSubscription(roundedRect?.value?.cornerRadius?.unit?.observe(resolver, callback)
                    ?: Disposable.NULL)
            }
        }
    }

    private fun DivSliderView.checkSliderTicks() {
        if (!visualErrorsEnabled || errorCollector == null) return
        doOnPreDraw {
            if (activeTickMarkDrawable != null || inactiveTickMarkDrawable != null) {
                val ticksNumber = maxValue - minValue
                val activeTicksWidth = activeTickMarkDrawable?.intrinsicWidth ?: 0
                val inactiveTicksWidth = inactiveTickMarkDrawable?.intrinsicWidth ?: 0
                val tickWidth = max(activeTicksWidth, inactiveTicksWidth)

                if (tickWidth * ticksNumber > width && errorCollector != null) {
                    val warnings = errorCollector!!.getWarnings()
                    var warningLogged = false
                    for (warning in warnings.iterator()) {
                        if (warning.message == SLIDER_TICKS_OVERLAP_WARNING) {
                            warningLogged = true
                        }
                    }
                    if (!warningLogged) {
                        errorCollector?.logWarning(Throwable(SLIDER_TICKS_OVERLAP_WARNING))
                    }
                }
            }
        }
    }
}

private fun DivDrawable.toDrawable(
    metrics: DisplayMetrics,
    resolver: ExpressionResolver
): Drawable? {
    return when (this) {
        is DivDrawable.Shape -> value.toDrawable(metrics, resolver)
    }
}

private fun DivShapeDrawable.toDrawable(
    metrics: DisplayMetrics,
    resolver: ExpressionResolver
): Drawable? {
    return when (val shape = this.shape) {
        is DivShape.RoundedRectangle -> {
            RoundedRectDrawable(
                RoundedRectParams(
                    width = shape.value.itemWidth.toPxF(metrics, resolver),
                    height = shape.value.itemHeight.toPxF(metrics, resolver),
                    color = color.evaluate(resolver),
                    radius = shape.value.cornerRadius.toPxF(metrics, resolver),
                    strokeColor = stroke?.color?.evaluate(resolver),
                    strokeWidth = stroke?.width?.evaluate(resolver)?.toFloat()
                )
            )
        }
        else -> null
    }
}

private fun DivSlider.TextStyle.toSliderTextStyle(
    metrics: DisplayMetrics,
    typefaceProvider: DivTypefaceProvider,
    resolver: ExpressionResolver
): SliderTextStyle {
    return SliderTextStyle(
        fontSize = fontSize.evaluate(resolver).fontSizeToPx(fontSizeUnit.evaluate(resolver), metrics),
        fontWeight = getTypeface(fontWeight.evaluate(resolver), typefaceProvider),
        offsetX = offset?.x?.toPx(metrics, resolver)?.toFloat() ?: 0f,
        offsetY = offset?.y?.toPx(metrics, resolver)?.toFloat() ?: 0f,
        textColor = textColor.evaluate(resolver)
    )
}

