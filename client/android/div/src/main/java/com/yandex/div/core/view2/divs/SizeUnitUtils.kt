@file:JvmMultifileClass
@file:JvmName("BaseDivViewExtensionsKt")

package com.yandex.div.core.view2.divs

import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivDimension
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivSizeUnitValue
import kotlin.math.roundToInt

fun <T : Number> T?.dpToPxF(metrics: DisplayMetrics): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this?.toFloat() ?: 0f, metrics)

fun <T : Number> T?.spToPxF(metrics: DisplayMetrics): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this?.toFloat() ?: 0f, metrics)

fun <T : Number> T?.unitToPxF(metrics: DisplayMetrics, unit: DivSizeUnit): Float =
    TypedValue.applyDimension(unit.toAndroidUnit(), this?.toFloat() ?: 0f, metrics)

fun <T : Number> T?.pxToDpF(metrics: DisplayMetrics): Float {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        TypedValue.deriveDimension(TypedValue.COMPLEX_UNIT_DIP, this?.toFloat() ?: 0f, metrics)
    } else {
        (this?.toFloat() ?: 0f) / metrics.density
    }
}

fun <T : Number> T?.pxToSpF(metrics: DisplayMetrics): Float {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        TypedValue.deriveDimension(TypedValue.COMPLEX_UNIT_SP, this?.toFloat() ?: 0f, metrics)
    } else {
        (this?.toFloat() ?: 0f) / metrics.scaledDensity
    }
}

fun <T : Number> T?.dpToPx(metrics: DisplayMetrics): Int = dpToPxF(metrics).roundToInt()

fun <T : Number> T?.spToPx(metrics: DisplayMetrics): Int = spToPxF(metrics).roundToInt()

fun <T : Number> T?.unitToPx(metrics: DisplayMetrics, unit: DivSizeUnit): Int = unitToPxF(metrics, unit).roundToInt()

fun <T : Number> T?.pxToDp(metrics: DisplayMetrics): Int = pxToDpF(metrics).roundToInt()

fun <T : Number> T?.pxToSp(metrics: DisplayMetrics): Int = pxToSpF(metrics).roundToInt()

fun Long?.dpToPx(metrics: DisplayMetrics): Int = this?.toIntSafely().dpToPx(metrics)

fun Long?.spToPx(metrics: DisplayMetrics): Int = this?.toIntSafely().spToPx(metrics)

fun Long?.unitToPx(metrics: DisplayMetrics, unit: DivSizeUnit): Int = this?.toIntSafely().unitToPx(metrics, unit)

fun Long?.pxToDp(metrics: DisplayMetrics): Int = this?.toIntSafely().pxToDp(metrics)

fun Long?.pxToSp(metrics: DisplayMetrics): Int = this?.toIntSafely().pxToSp(metrics)

internal fun Long.toPx(unit: DivSizeUnit, metrics: DisplayMetrics): Int {
    return when (unit) {
        DivSizeUnit.DP -> dpToPx(metrics)
        DivSizeUnit.SP -> spToPx(metrics)
        DivSizeUnit.PX -> toIntSafely()
    }
}

internal fun DivFixedSize.toPx(metrics: DisplayMetrics, resolver: ExpressionResolver): Int {
    return when (unit.evaluate(resolver)) {
        DivSizeUnit.DP -> value.evaluate(resolver).dpToPx(metrics)
        DivSizeUnit.SP -> value.evaluate(resolver).spToPx(metrics)
        DivSizeUnit.PX -> value.evaluate(resolver).toIntSafely()
    }
}

internal fun DivSizeUnitValue.toPx(metrics: DisplayMetrics, resolver: ExpressionResolver): Int {
    return when (unit.evaluate(resolver)) {
        DivSizeUnit.DP -> value.evaluate(resolver).dpToPx(metrics)
        DivSizeUnit.SP -> value.evaluate(resolver).spToPx(metrics)
        DivSizeUnit.PX -> value.evaluate(resolver).toIntSafely()
    }
}

internal fun DivFixedSize.toPxF(
    metrics: DisplayMetrics,
    resolver: ExpressionResolver
): Float = value.evaluate(resolver).toPxF(unit.evaluate(resolver), metrics)

internal fun Long.toPxF(
    unit: DivSizeUnit,
    metrics: DisplayMetrics
): Float {
    return when (unit) {
        DivSizeUnit.DP -> dpToPxF(metrics)
        DivSizeUnit.SP -> spToPxF(metrics)
        DivSizeUnit.PX -> toFloat()
    }
}

internal fun DivDimension.toPx(metrics: DisplayMetrics, resolver: ExpressionResolver): Int {
    return when (unit.evaluate(resolver)) {
        DivSizeUnit.DP -> value.evaluate(resolver).dpToPx(metrics)
        DivSizeUnit.SP -> value.evaluate(resolver).spToPx(metrics)
        DivSizeUnit.PX -> value.evaluate(resolver).toInt()
    }
}

internal fun DivSizeUnit.toAndroidUnit(): Int {
    return when (this) {
        DivSizeUnit.DP -> TypedValue.COMPLEX_UNIT_DIP
        DivSizeUnit.SP -> TypedValue.COMPLEX_UNIT_SP
        DivSizeUnit.PX -> TypedValue.COMPLEX_UNIT_PX
    }
}

internal fun Long.fontSizeToPx(unit: DivSizeUnit, metrics: DisplayMetrics): Float {
    return when (unit) {
        DivSizeUnit.DP -> this.dpToPx(metrics)
        DivSizeUnit.SP -> this.spToPx(metrics)
        DivSizeUnit.PX -> this
    }.toFloat()
}
