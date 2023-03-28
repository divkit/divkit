@file:Suppress("UNUSED", "NOTHING_TO_INLINE")

package com.yandex.div.internal.util

import android.content.Context
import android.content.res.Resources
import android.view.View
import androidx.annotation.DimenRes
import androidx.annotation.Dimension
import androidx.annotation.Px
import kotlin.math.roundToInt

private val metrics = Resources.getSystem().displayMetrics

/**
 * All the helpers for size in android are here.
 *
 * - Use imperative style helpers for imperative code, when converting one type of dimension to another:
 *
 * e.g.
 * canvas.drawCircle(x, y, dpToPx(MAX_OUTER_RADIUS_DP) * currentAnimationScale, outerPaint)
 * OR
 * canvas.drawCircle(x, y, MAX_OUTER_RADIUS_DP.dpToPx() * currentAnimationScale, outerPaint)
 *
 * - Use declarative style in DSL and when setting sizes directly:
 *
 * e.g.
 * textView.apply {
 *     width = dp(85)
 *     height = dp(40)
 *     margin = dp(10)
 *     textSize = sp(12)
 * }
 */

// region Imperative Style Helpers
@Px
fun dpToPx(@Dimension(unit = Dimension.DP) value: Int): Int = (value * metrics.density).roundToInt()
@Px
fun dpToPx(@Dimension(unit = Dimension.DP) value: Long): Int = (value * metrics.density).roundToInt()
@Px
fun dpToPx(@Dimension(unit = Dimension.DP) value: Float): Float = value * metrics.density

@Px
fun spToPx(@Dimension(unit = Dimension.SP) value: Int): Int = (value * metrics.scaledDensity).roundToInt()
@Px
fun spToPx(@Dimension(unit = Dimension.SP) value: Float): Float = value * metrics.scaledDensity

@Dimension(unit = Dimension.DP)
fun pxToDp(@Px px: Int): Int = (px / metrics.density).toInt()
@Dimension(unit = Dimension.DP)
fun pxToDp(@Px px: Float): Float = px / metrics.density

@Px
@JvmName("dp2Px")
inline fun @receiver:Dimension(unit = Dimension.DP) Int.dpToPx(): Int = dpToPx(this)
@Px
@JvmName("dp2Px")
inline fun @receiver:Dimension(unit = Dimension.DP) Float.dpToPx(): Float = dpToPx(this)

@Px
@JvmName("sp2Px")
inline fun @receiver:Dimension(unit = Dimension.DP) Int.spToPx(): Int = spToPx(this)
@Px
@JvmName("sp2Px")
inline fun @receiver:Dimension(unit = Dimension.DP)  Float.spToPx(): Float = spToPx(this)

@Dimension(unit = Dimension.DP)
@JvmName("px2Dp")
inline fun @receiver:Px Int.pxToDp(): Int = pxToDp(this)
// endregion

// region Declarative style Helpers
@Px
fun dp(@Dimension(unit = Dimension.DP) value: Int): Int = (value * metrics.density).roundToInt()
@Px
fun dp(@Dimension(unit = Dimension.DP) value: Float): Int = (value * metrics.density).roundToInt()

@Px
fun sp(@Dimension(unit = Dimension.SP) value: Int): Int = (value * metrics.scaledDensity).roundToInt()
@Px
fun sp(@Dimension(unit = Dimension.SP) value: Float): Int = (value * metrics.scaledDensity).roundToInt()

@Px
fun dpF(@Dimension(unit = Dimension.DP) value: Int): Float = value * metrics.density
@Px
fun dpF(@Dimension(unit = Dimension.DP) value: Float): Float = value * metrics.density

@Px
fun spF(@Dimension(unit = Dimension.SP) value: Int): Float = value * metrics.scaledDensity
@Px
fun spF(@Dimension(unit = Dimension.SP) value: Float): Float = value * metrics.scaledDensity
// endregion

// region Resource Helpers
@Px
fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)
@Px
fun Context.dimenF(@DimenRes resource: Int): Float = resources.getDimensionPixelSize(resource).toFloat()

@Px
inline fun View.dimen(@DimenRes resource: Int): Int = context.dimen(resource)
@Px
inline fun View.dimenF(@DimenRes resource: Int): Float = context.dimenF(resource)

fun Context.dimenSize(@DimenRes resource: Int): Size = Size.dimen(this, resource)
fun View.dimenSize(@DimenRes resource: Int): Size = Size.dimen(context, resource)
// endregion

// region Inline Class (experimental)
@JvmInline
value class Size(@Px val px: Int) {

    @get:Dimension(unit = Dimension.DP)
    val dp: Int get() = (px / metrics.density).toInt()
    @get:Dimension(unit = Dimension.DP)
    val dpF: Float get() = px.toFloat() / metrics.density

    @get:Dimension(unit = Dimension.SP)
    val sp: Int get() = (px / metrics.scaledDensity).toInt()
    @get:Dimension(unit = Dimension.SP)
    val spF: Float get() = px / metrics.scaledDensity

    companion object {
        fun px(@Px value: Int) = Size(value)
        fun dp(@Dimension(unit = Dimension.DP) value: Int) = Size((value * metrics.density).toInt())
        fun dp(@Dimension(unit = Dimension.DP) value: Float) = Size((value * metrics.density).toInt())
        fun sp(@Dimension(unit = Dimension.SP) value: Int) = Size((value * metrics.scaledDensity).toInt())
        fun sp(@Dimension(unit = Dimension.SP) value: Float) = Size((value * metrics.scaledDensity).toInt())
        fun dimen(context: Context, @DimenRes resId: Int) = Size(context.dimen(resId))
    }
}
// endregion
