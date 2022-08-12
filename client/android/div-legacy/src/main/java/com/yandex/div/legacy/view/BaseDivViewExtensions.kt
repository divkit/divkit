package com.yandex.div.legacy.view

import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import com.yandex.alicekit.core.utils.fontHeight
import com.yandex.alicekit.core.widget.TabView
import com.yandex.alicekit.core.widget.TypefaceType
import com.yandex.div.core.images.LoadReference
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.legacy.R
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivTabs
import kotlin.math.roundToInt

//todo move me to core-views when alicekit gonna be removed from deps
@Suppress("UNCHECKED_CAST")
internal fun View.saveLoadReference(reference: LoadReference) {
    val references = getTag(R.id.load_references_tag)
    if (references == null) {
        setTag(R.id.load_references_tag, mutableSetOf(reference))
    } else {
        val referenceSet = references as MutableSet<LoadReference>
        referenceSet.add(reference)
    }
}

fun TabView.applyStyle(style: DivTabs.TabTitleStyle) {
    applyFontSize(style.fontSize.evaluate(ExpressionResolver.EMPTY), style.fontSizeUnit.evaluate(ExpressionResolver.EMPTY))
    applyLetterSpacing(style.letterSpacing.evaluate(ExpressionResolver.EMPTY))
    applyLineHeight(style.lineHeight?.evaluate(ExpressionResolver.EMPTY))

    includeFontPadding = false
    val paddings = style.paddings
    val metrics = resources.displayMetrics
    setTabPadding(
        paddings.left.evaluate(ExpressionResolver.EMPTY).dpToPx(metrics), paddings.top.evaluate(ExpressionResolver.EMPTY).dpToPx(metrics),
        paddings.right.evaluate(ExpressionResolver.EMPTY).dpToPx(metrics), paddings.bottom.evaluate(ExpressionResolver.EMPTY).dpToPx(metrics)
    )
    when (style.fontWeight.evaluate(ExpressionResolver.EMPTY)) {
        DivFontWeight.MEDIUM -> setDefaultTypefaceType(TypefaceType.MEDIUM)
        DivFontWeight.REGULAR -> setDefaultTypefaceType(TypefaceType.REGULAR)
        DivFontWeight.LIGHT -> setDefaultTypefaceType(TypefaceType.LIGHT)
        DivFontWeight.BOLD -> setDefaultTypefaceType(TypefaceType.BOLD)
    }
}

fun TextView.applyFontSize(fontSize: Int, unit: DivSizeUnit) {
    setTextSize(unit.toAndroidUnit(), fontSize.toFloat())
}

fun DivSizeUnit.toAndroidUnit(): Int {
    return when (this) {
        DivSizeUnit.DP -> TypedValue.COMPLEX_UNIT_DIP
        DivSizeUnit.SP -> TypedValue.COMPLEX_UNIT_SP
        DivSizeUnit.PX -> TypedValue.COMPLEX_UNIT_PX
    }
}

fun TextView.applyLetterSpacing(letterSpacing: Double) {
    this.letterSpacing = letterSpacing.toFloat()
}

fun TextView.applyLineHeight(lineHeight: Int?) {
    val lineSpacingExtra =
        lineHeight?.let { it.spToPx(resources.displayMetrics) - this.fontHeight } ?: 0
    setLineSpacing(lineSpacingExtra.toFloat(), 1f)
}

fun Int?.dpToPx(metrics: DisplayMetrics): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this?.toFloat() ?: 0f, metrics)
        .roundToInt()
}

fun Int?.dpToPxF(metrics: DisplayMetrics): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this?.toFloat() ?: 0f, metrics)
}

fun Double?.dpToPx(metrics: DisplayMetrics): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this?.toFloat() ?: 0f, metrics)
        .roundToInt()
}

fun Int?.spToPx(metrics: DisplayMetrics): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this?.toFloat() ?: 0f, metrics)
        .roundToInt()
}

fun Int?.spToPxF(metrics: DisplayMetrics): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this?.toFloat() ?: 0f, metrics)
}

fun Double?.spToPx(metrics: DisplayMetrics): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this?.toFloat() ?: 0f, metrics)
        .roundToInt()
}

fun Int?.unitToPx(metrics: DisplayMetrics, unit: DivSizeUnit): Int {
    return TypedValue.applyDimension(unit.toAndroidUnit(), this?.toFloat() ?: 0f, metrics)
        .roundToInt()
}
