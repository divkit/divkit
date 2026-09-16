package com.yandex.div.core.view2.divs

import android.widget.TextView
import com.yandex.div.core.util.colorsEqualToConstant
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.util.isConstantOrNull
import com.yandex.div.core.util.observeColorPoint
import com.yandex.div.core.util.observeRadialGradientCenter
import com.yandex.div.core.util.observeRadialGradientRadius
import com.yandex.div.core.util.toColormap
import com.yandex.div.core.util.toRadialGradientDrawableCenter
import com.yandex.div.core.util.toRadialGradientDrawableRadius
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.AnimatedTextGradientData
import com.yandex.div.core.view2.divs.widgets.DivLineHeightTextView
import com.yandex.div.internal.drawable.LinearGradientDrawable
import com.yandex.div.internal.drawable.RadialGradientDrawable
import com.yandex.div.internal.graphics.Colormap
import com.yandex.div.internal.graphics.checkIsNotEmpty
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstant
import com.yandex.div.json.expressions.isConstantOrNull
import com.yandex.div2.DivAnimatedTextGradient
import com.yandex.div2.DivLinearGradient
import com.yandex.div2.DivRadialGradient
import com.yandex.div2.DivStaticTextGradient
import com.yandex.div2.DivText
import com.yandex.div2.DivTextGradient

internal object TextGradientBinder {

    fun bind(
        view: DivLineHeightTextView,
        divView: Div2View,
        newDiv: DivText,
        oldDiv: DivText?,
        resolver: ExpressionResolver,
    ) = with(view) {
        when (val textGradient = newDiv.textGradient) {
            null -> clearTextGradient()
            is DivTextGradient.Linear -> {
                clearAnimatedTextGradient()
                bindLinearTextGradient(divView, textGradient.value, oldDiv?.textGradient, resolver)
            }
            is DivTextGradient.Radial -> {
                clearAnimatedTextGradient()
                bindRadialTextGradient(divView, textGradient.value, oldDiv?.textGradient, resolver)
            }
            is DivTextGradient.Animated -> bindAnimatedTextGradient(divView, textGradient.value, resolver)
        }
    }

    private fun DivLineHeightTextView.clearTextGradient() {
        clearAnimatedTextGradient()
        paint.shader = null
        invalidate()
    }

    private fun DivLineHeightTextView.bindAnimatedTextGradient(
        divView: Div2View,
        textGradient: DivAnimatedTextGradient,
        resolver: ExpressionResolver,
    ) {
        val animationsEnabledController = divView.div2Component.animationsEnabledController

        applyAnimatedTextGradient(divView, textGradient, resolver)
        val callback = { _: Any -> applyAnimatedTextGradient(divView, textGradient, resolver) }
        addSubscription(textGradient.duration.observe(resolver, callback))
        subscribeToAnimatedGradient(textGradient.gradient, resolver, callback)
        addSubscription(
            animationsEnabledController.observe(divView) {
                setTextGradientAnimationsEnabled(animationsEnabledController.isEnabled())
            }
        )
    }

    private fun DivLineHeightTextView.applyAnimatedTextGradient(
        divView: Div2View,
        textGradient: DivAnimatedTextGradient,
        resolver: ExpressionResolver,
    ) {
        val gradientData = textGradient.resolveGradientData(resolver, divView)
        if (gradientData == null) {
            clearTextGradient()
        } else {
            setAnimatedTextGradient(gradientData, divView.div2Component.animationsEnabledController.isEnabled())
        }
    }

    private fun DivLineHeightTextView.subscribeToAnimatedGradient(
        gradient: DivStaticTextGradient,
        resolver: ExpressionResolver,
        callback: (Any) -> Unit,
    ) {
        when (gradient) {
            is DivStaticTextGradient.Linear -> {
                addSubscription(gradient.value.angle.observe(resolver, callback))
                addSubscription(gradient.value.colors?.observe(resolver, callback))
                gradient.value.colorMap?.forEach {
                    addSubscription(it.color.observe(resolver, callback))
                    addSubscription(it.position.observe(resolver, callback))
                }
            }
            is DivStaticTextGradient.Radial -> {
                addSubscription(gradient.value.colors?.observe(resolver, callback))
                gradient.value.colorMap?.forEach {
                    addSubscription(it.color.observe(resolver, callback))
                    addSubscription(it.position.observe(resolver, callback))
                }
                observeRadialGradientCenter(gradient.value.centerX, resolver, callback)
                observeRadialGradientCenter(gradient.value.centerY, resolver, callback)
                observeRadialGradientRadius(gradient.value.radius, resolver, callback)
            }
        }
    }

    private fun DivAnimatedTextGradient.resolveGradientData(
        resolver: ExpressionResolver,
        divView: Div2View,
    ): AnimatedTextGradientData? {
        val displayMetrics = divView.resources.displayMetrics
        val gradientData = when (val gradient = gradient) {
            is DivStaticTextGradient.Linear -> {
                val colormap = gradient.value.toColormap(resolver).checkIsNotEmpty(divView)
                if (colormap.colors.isEmpty()) {
                    return null
                }
                AnimatedTextGradientData.Gradient.Linear(
                    colormap = colormap,
                    angle = gradient.value.angle.evaluate(resolver).toFloat(),
                )
            }
            is DivStaticTextGradient.Radial -> {
                val colormap = gradient.value.toColormap(resolver).checkIsNotEmpty(divView)
                if (colormap.colors.isEmpty()) {
                    return null
                }
                AnimatedTextGradientData.Gradient.Radial(
                    colormap = colormap,
                    radius = gradient.value.radius.toRadialGradientDrawableRadius(displayMetrics, resolver),
                    centerX = gradient.value.centerX.toRadialGradientDrawableCenter(displayMetrics, resolver),
                    centerY = gradient.value.centerY.toRadialGradientDrawableCenter(displayMetrics, resolver),
                )
            }
        }
        return AnimatedTextGradientData(
            gradient = gradientData,
            duration = duration.evaluate(resolver),
        )
    }

    private fun DivLineHeightTextView.bindLinearTextGradient(
        divView: Div2View,
        newTextGradient: DivLinearGradient,
        oldTextGradient: DivTextGradient?,
        resolver: ExpressionResolver,
    ) {
        if (oldTextGradient is DivTextGradient.Linear
            && newTextGradient.angle.equalsToConstant(oldTextGradient.value.angle)
            && newTextGradient.colorsEqualToConstant(oldTextGradient.value)) {
            return
        }

        applyLinearTextGradientColor(
            newTextGradient.angle.evaluate(resolver),
            newTextGradient.toColormap(resolver).checkIsNotEmpty(divView)
        )

        if (newTextGradient.angle.isConstant()
            && newTextGradient.colors.isConstantOrNull()
            && newTextGradient.colorMap.isConstantOrNull()) {
            return
        }

        val callback = { _: Any ->
            applyLinearTextGradientColor(
                newTextGradient.angle.evaluate(resolver),
                newTextGradient.toColormap(resolver).checkIsNotEmpty(divView)
            )
        }
        addSubscription(newTextGradient.angle.observe(resolver, callback))
        addSubscription(newTextGradient.colors?.observe(resolver, callback))
        newTextGradient.colorMap?.forEach { observeColorPoint(it, resolver, callback) }
    }

    private fun TextView.applyLinearTextGradientColor(angle: Long, colormap: Colormap) {
        doOnActualLayout {
            paint.shader = LinearGradientDrawable.createLinearGradient(
                angle = angle.toFloat(),
                colors = colormap.colors,
                positions = colormap.positions,
                width = staticTextGradientWidth,
                height = height - paddingBottom - paddingTop,
            )
            invalidate()
        }
    }

    private fun DivLineHeightTextView.bindRadialTextGradient(
        divView: Div2View,
        newTextGradient: DivRadialGradient,
        oldTextGradient: DivTextGradient?,
        resolver: ExpressionResolver,
    ) {
        // TODO: compare radius and center in a proper way
        if (oldTextGradient is DivTextGradient.Radial
            && newTextGradient.radius == oldTextGradient.value.radius
            && newTextGradient.centerX == oldTextGradient.value.centerX
            && newTextGradient.centerY == oldTextGradient.value.centerY
            && newTextGradient.colorsEqualToConstant(oldTextGradient.value)) {
            return
        }

        val displayMetrics = resources.displayMetrics
        applyRadialTextGradientColor(
            newTextGradient.radius.toRadialGradientDrawableRadius(displayMetrics, resolver),
            newTextGradient.centerX.toRadialGradientDrawableCenter(displayMetrics, resolver),
            newTextGradient.centerY.toRadialGradientDrawableCenter(displayMetrics, resolver),
            newTextGradient.toColormap(resolver).checkIsNotEmpty(divView),
        )

        val colorMapConst = newTextGradient.colorMap.isConstantOrNull()
        if (newTextGradient.colors.isConstantOrNull() && colorMapConst) {
            return
        }

        val callback = { _: Any ->
            applyRadialTextGradientColor(
                radius = newTextGradient.radius.toRadialGradientDrawableRadius(displayMetrics, resolver),
                centerX = newTextGradient.centerX.toRadialGradientDrawableCenter(displayMetrics, resolver),
                centerY = newTextGradient.centerY.toRadialGradientDrawableCenter(displayMetrics, resolver),
                colormap = newTextGradient.toColormap(resolver).checkIsNotEmpty(divView),
            )
        }
        addSubscription(newTextGradient.colors?.observe(resolver, callback))
        newTextGradient.colorMap?.forEach { observeColorPoint(it, resolver, callback) }
    }

    private fun TextView.applyRadialTextGradientColor(
        radius: RadialGradientDrawable.Radius,
        centerX: RadialGradientDrawable.Center,
        centerY: RadialGradientDrawable.Center,
        colormap: Colormap,
    ) {
        doOnActualLayout {
            paint.shader = RadialGradientDrawable.createRadialGradient(
                radius = radius,
                centerX = centerX,
                centerY = centerY,
                colors = colormap.colors,
                positions = colormap.positions,
                width = staticTextGradientWidth,
                height = height - paddingBottom - paddingTop,
            )
            invalidate()
        }
    }

    private val TextView.staticTextGradientWidth: Int
        get() = minOf(availableWidth, paint.measureText(text.toString()).toInt())
}
