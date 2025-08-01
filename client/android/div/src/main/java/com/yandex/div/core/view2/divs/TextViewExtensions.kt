package com.yandex.div.core.view2.divs

import android.os.Build
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.DivTypefaceResolver
import com.yandex.div.core.view2.getTypeface
import com.yandex.div.core.view2.getTypefaceValue
import com.yandex.div.core.widget.FixedLineHeightView
import com.yandex.div.core.widget.FixedLineHeightView.Companion.UNDEFINED_LINE_HEIGHT
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.util.forEach
import com.yandex.div.internal.util.isEmpty
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstant
import com.yandex.div.json.expressions.isConstantOrNull
import com.yandex.div2.DivBase
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivSizeUnit
import org.json.JSONObject

internal fun <T> T.observeBaseTextProperties(
    newFontSize: Expression<Long>,
    newFontSizeUnit: Expression<DivSizeUnit>,
    newLetterSpacing: Expression<Double>,
    newTextColor: Expression<Int>,
    newLineHeight: Expression<Long>?,
    newFontFamily: Expression<String>?,
    newFontWeight: Expression<DivFontWeight>?,
    newFontWeightValue: Expression<Long>?,
    newFontVariationSettings: Expression<JSONObject>?,
    oldFontSize: Expression<Long>?,
    oldFontSizeUnit: Expression<DivSizeUnit>?,
    oldLetterSpacing: Expression<Double>?,
    oldTextColor: Expression<Int>?,
    oldLineHeight: Expression<Long>?,
    oldFontFamily: Expression<String>?,
    oldFontWeight: Expression<DivFontWeight>?,
    oldFontWeightValue: Expression<Long>?,
    oldFontVariationSettings: Expression<JSONObject>?,
    oldDiv: DivBase?,
    typefaceResolver: DivTypefaceResolver,
    resolver: ExpressionResolver,
) where T : TextView, T : FixedLineHeightView, T : ExpressionSubscriber {
    observeFontSize(newFontSize, newFontSizeUnit, oldFontSize, oldFontSizeUnit, resolver, this)
    observeLetterSpacing(newLetterSpacing, newFontSize, oldLetterSpacing, oldFontSize, resolver, this)
    observeTextColor(newTextColor, oldTextColor, resolver)
    observeLineHeight(newLineHeight, newFontSizeUnit, oldLineHeight, oldFontSizeUnit, resolver, this)
    observeTypeface(
        newFontFamily,
        newFontWeight,
        newFontWeightValue,
        newFontVariationSettings,
        oldFontFamily,
        oldFontWeight,
        oldFontWeightValue,
        oldFontVariationSettings,
        oldDiv,
        typefaceResolver,
        resolver,
    )
}

internal fun TextView.observeFontSize(
    newFontSize: Expression<Long>,
    newFontSizeUnit: Expression<DivSizeUnit>,
    oldFontSize: Expression<Long>?,
    oldFontSizeUnit: Expression<DivSizeUnit>?,
    resolver: ExpressionResolver,
    subscriber: ExpressionSubscriber,
) {
    if (newFontSize.equalsToConstant(oldFontSize) && newFontSizeUnit.equalsToConstant(oldFontSizeUnit)) return

    applyFontSize(newFontSize, newFontSizeUnit, resolver)

    if (newFontSize.isConstant() && newFontSizeUnit.isConstant()) return

    val callback = { _: Any -> applyFontSize(newFontSize, newFontSizeUnit, resolver) }
    subscriber.addSubscription(newFontSize.observe(resolver, callback))
    subscriber.addSubscription(newFontSizeUnit.observe(resolver, callback))
}

private fun TextView.applyFontSize(
    fontSizeExpr: Expression<Long>,
    fontSizeUnitExpr: Expression<DivSizeUnit>,
    resolver: ExpressionResolver
) = setTextSize(fontSizeUnitExpr.evaluate(resolver).toAndroidUnit(), fontSizeExpr.evaluate(resolver).toFloat())

internal fun TextView.observeLetterSpacing(
    newLetterSpacing: Expression<Double>,
    newFontSize: Expression<Long>,
    oldLetterSpacing: Expression<Double>?,
    oldFontSize: Expression<Long>?,
    resolver: ExpressionResolver,
    subscriber: ExpressionSubscriber,
) {
    if (newLetterSpacing.equalsToConstant(oldLetterSpacing) && newFontSize.equalsToConstant(oldFontSize)) return

    applyLetterSpacing(newLetterSpacing, newFontSize, resolver)

    if (newLetterSpacing.isConstant() && newFontSize.isConstant()) return

    val callback = { _: Any -> applyLetterSpacing(newLetterSpacing, newFontSize, resolver) }
    subscriber.addSubscription(newFontSize.observe(resolver, callback))
    subscriber.addSubscription(newLetterSpacing.observe(resolver, callback))
}

private fun TextView.applyLetterSpacing(
    letterSpacingExpr: Expression<Double>,
    fontSizeExpr: Expression<Long>,
    resolver: ExpressionResolver
) {
    letterSpacing = letterSpacingExpr.evaluate(resolver).toFloat() / fontSizeExpr.evaluate(resolver)
}

internal fun <T> T.observeTextColor(
    newTextColor: Expression<Int>,
    oldTextColor: Expression<Int>?,
    resolver: ExpressionResolver,
) where T : TextView, T : ExpressionSubscriber {
    if (newTextColor.equalsToConstant(oldTextColor)) return

    setTextColor(newTextColor.evaluate(resolver))

    if (newTextColor.isConstant()) return

    addSubscription(
        newTextColor.observe(resolver) { setTextColor(it) }
    )
}

internal fun <T> T.observeLineHeight(
    newLineHeight: Expression<Long>?,
    newFontSizeUnit: Expression<DivSizeUnit>,
    oldLineHeight: Expression<Long>?,
    oldFontSizeUnit: Expression<DivSizeUnit>?,
    resolver: ExpressionResolver,
    subscriber: ExpressionSubscriber,
) where T : TextView, T : FixedLineHeightView {
    if (newLineHeight.equalsToConstant(oldLineHeight) && newFontSizeUnit.equalsToConstant(oldFontSizeUnit)) return

    applyLineHeight(newLineHeight, newFontSizeUnit, resolver)

    if (newLineHeight == null || (newLineHeight.isConstant() && newFontSizeUnit.isConstant())) return

    val callback = { _: Any -> applyLineHeight(newLineHeight, newFontSizeUnit, resolver) }
    subscriber.addSubscription(newLineHeight.observe(resolver, callback))
    subscriber.addSubscription(newFontSizeUnit.observe(resolver, callback))
}

private fun <T> T.applyLineHeight(
    lineHeightExpr: Expression<Long>?,
    unitExpr: Expression<DivSizeUnit>,
    resolver: ExpressionResolver,
) where T : TextView, T : FixedLineHeightView {
    fixedLineHeight = lineHeightExpr?.evaluate(resolver)
        ?.unitToPx(resources.displayMetrics, unitExpr.evaluate(resolver))
        ?: UNDEFINED_LINE_HEIGHT
}

internal fun <T> T.observeTypeface(
    newFontFamily: Expression<String>?,
    newFontWeight: Expression<DivFontWeight>?,
    newFontWeightValue: Expression<Long>?,
    newFontVariationSettings: Expression<JSONObject>?,
    oldFontFamily: Expression<String>?,
    oldFontWeight: Expression<DivFontWeight>?,
    oldFontWeightValue: Expression<Long>?,
    oldFontVariationSettings: Expression<JSONObject>?,
    oldDiv: DivBase?,
    typefaceResolver: DivTypefaceResolver,
    resolver: ExpressionResolver,
) where T : TextView, T : ExpressionSubscriber {
    if (oldDiv != null && newFontFamily.equalsToConstant(oldFontFamily) &&
        newFontWeight.equalsToConstant(oldFontWeight) &&
        newFontWeightValue.equalsToConstant(oldFontWeightValue) &&
        (!supportFontVariations || newFontVariationSettings.equalsToConstant(oldFontVariationSettings))) {
        return
    }

    applyTypeface(
        newFontFamily,
        newFontWeight,
        newFontWeightValue,
        newFontVariationSettings,
        typefaceResolver,
        resolver
    )

    observeTypeface(
        newFontFamily,
        newFontWeight,
        newFontWeightValue,
        newFontVariationSettings,
        typefaceResolver,
        resolver
    )
    observeFontVariationSettings(newFontWeight, newFontWeightValue, newFontVariationSettings, resolver)
}

private fun TextView.applyTypeface(
    fontFamily: Expression<String>?,
    fontWeight: Expression<DivFontWeight>?,
    fontWeightValue: Expression<Long>?,
    fontVariations: Expression<JSONObject>?,
    typefaceResolver: DivTypefaceResolver,
    resolver: ExpressionResolver
) {
    val typefaceProvider = typefaceResolver.getTypefaceProvider(fontFamily?.evaluate(resolver))
    typeface = getTypeface(
        fontWeight?.evaluate(resolver),
        fontWeightValue?.evaluate(resolver)?.toIntSafely(),
        typefaceProvider,
    )

    if (!supportFontVariations || !typefaceProvider.isVariable) return

    fontVariationSettings = null
    applyFontVariationSettings(fontWeight, fontWeightValue, fontVariations, resolver)
}

private fun <T> T.observeTypeface(
    fontFamily: Expression<String>?,
    fontWeight: Expression<DivFontWeight>?,
    fontWeightValue: Expression<Long>?,
    fontVariations: Expression<JSONObject>?,
    typefaceResolver: DivTypefaceResolver,
    resolver: ExpressionResolver
) where T : TextView, T : ExpressionSubscriber {
    if (fontFamily.isConstantOrNull() && fontWeight.isConstantOrNull() && fontWeightValue.isConstantOrNull()) return

    val callback = { _: Any ->
        applyTypeface(
            fontFamily,
            fontWeight,
            fontWeightValue,
            fontVariations,
            typefaceResolver,
            resolver
        )
    }
    fontFamily?.let { addSubscription(it.observe(resolver, callback)) }
    fontWeight?.let { addSubscription(it.observe(resolver, callback)) }
    fontWeightValue?.let { addSubscription(it.observe(resolver, callback)) }
}

internal fun <T> T.observeFontVariationSettings(
    newFontWeight: Expression<DivFontWeight>?,
    newFontWeightValue: Expression<Long>?,
    newFontVariationSettings: Expression<JSONObject>?,
    resolver: ExpressionResolver,
) where T : TextView, T : ExpressionSubscriber {
    if (!supportFontVariations || newFontVariationSettings.isConstantOrNull()) return

    addSubscription(
        newFontVariationSettings?.observe(resolver) {
            applyFontVariationSettings(newFontWeight, newFontWeightValue, newFontVariationSettings, resolver)
        }
    )
}

internal val supportFontVariations get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O

@RequiresApi(Build.VERSION_CODES.O)
private fun TextView.applyFontVariationSettings(
    fontWeight: Expression<DivFontWeight>?,
    fontWeightValue: Expression<Long>?,
    fontVariations: Expression<JSONObject>?,
    resolver: ExpressionResolver,
) {
    fontVariationSettings = getFontVariations(fontWeight, fontWeightValue, fontVariations, resolver)
}

internal fun getFontVariations(
    fontWeight: Expression<DivFontWeight>?,
    fontWeightValue: Expression<Long>?,
    fontVariations: Expression<JSONObject>?,
    resolver: ExpressionResolver,
) = getFontVariations(
    fontWeight?.evaluate(resolver),
    fontWeightValue?.evaluate(resolver)?.toIntSafely(),
    fontVariations?.evaluate(resolver)
)

private const val WEIGHT_AXIS = "wght"

internal fun getFontVariations(
    fontWeight: DivFontWeight?,
    fontWeightValue: Int?,
    fontVariations: JSONObject?,
): String? {
    val hasWeight = fontWeight != null || fontWeightValue != null
    val weight = getTypefaceValue(fontWeight, fontWeightValue)

    if (fontVariations?.isEmpty() != false) {
        return if (hasWeight) getVariation(WEIGHT_AXIS, weight) else null
    }

    if (hasWeight && !fontVariations.has(WEIGHT_AXIS)) {
        fontVariations.put(WEIGHT_AXIS, weight)
    }
    return buildList {
        fontVariations.forEach { axis, value: Number -> add(getVariation(axis, value)) }
    }.joinToString(", ")
}

private fun getVariation(axis: String, value: Number) = "'$axis' $value"
