package com.yandex.div.core.view2.divs

import android.graphics.Canvas
import android.graphics.Typeface
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.core.graphics.withTranslation
import androidx.core.view.ViewCompat
import androidx.core.view.children
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import com.yandex.div.core.expression.ExpressionSubscriber
import com.yandex.div.core.expression.suppressExpressionErrors
import com.yandex.div.core.util.Log
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.widgets.DivBorderDrawer
import com.yandex.div.core.view2.divs.widgets.DivBorderSupports
import com.yandex.div.core.widget.AspectImageView
import com.yandex.div.core.widget.GridContainer
import com.yandex.div.core.widget.wraplayout.WrapAlignment
import com.yandex.div.core.widget.wraplayout.WrapLayout
import com.yandex.div.drawables.ScalingDrawable
import com.yandex.div.font.DivTypefaceProvider
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.util.dpToPx
import com.yandex.div.util.fontHeight
import com.yandex.div.util.spToPx
import com.yandex.div2.Div
import com.yandex.div2.DivAction
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivAnimation
import com.yandex.div2.DivBase
import com.yandex.div2.DivBorder
import com.yandex.div2.DivContainer
import com.yandex.div2.DivDimension
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivImageScale
import com.yandex.div2.DivPivot
import com.yandex.div2.DivPivotFixed
import com.yandex.div2.DivPivotPercentage
import com.yandex.div2.DivRadialGradientCenter
import com.yandex.div2.DivRadialGradientFixedCenter
import com.yandex.div2.DivRadialGradientRadius
import com.yandex.div2.DivRadialGradientRelativeCenter
import com.yandex.div2.DivRadialGradientRelativeRadius
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivVisibilityAction
import kotlin.math.roundToInt

fun View.applyPaddings(insets: DivEdgeInsets?, resolver: ExpressionResolver) {
    val metrics = resources.displayMetrics
    when(insets?.unit?.evaluate(resolver)) {
        DivSizeUnit.DP -> {
            setPadding(
                insets.left.evaluate(resolver).dpToPx(metrics), insets.top.evaluate(resolver).dpToPx(metrics),
                insets.right.evaluate(resolver).dpToPx(metrics), insets.bottom.evaluate(resolver).dpToPx(metrics))
        }
        DivSizeUnit.SP -> {
            setPadding(
                insets.left.evaluate(resolver).spToPx(metrics), insets.top.evaluate(resolver).spToPx(metrics),
                insets.right.evaluate(resolver).spToPx(metrics), insets.bottom.evaluate(resolver).spToPx(metrics))
        }
        DivSizeUnit.PX -> {
            setPadding(
                insets.left.evaluate(resolver), insets.top.evaluate(resolver),
                insets.right.evaluate(resolver), insets.bottom.evaluate(resolver))
        }
    }
}

fun View.applyMargins(insets: DivEdgeInsets?, resolver: ExpressionResolver) {
    val metrics = resources.displayMetrics
    val lp = layoutParams as? ViewGroup.MarginLayoutParams ?: return

    var leftMargin = 0
    var topMargin = 0
    var rightMargin = 0
    var bottomMargin = 0

    if (insets != null) {
        val unit = insets.unit.evaluate(resolver)
        leftMargin = insets.left.evaluate(resolver).unitToPx(metrics, unit)
        topMargin = insets.top.evaluate(resolver).unitToPx(metrics, unit)
        rightMargin = insets.right.evaluate(resolver).unitToPx(metrics, unit)
        bottomMargin = insets.bottom.evaluate(resolver).unitToPx(metrics, unit)
    }

    if (lp.leftMargin != leftMargin || lp.topMargin != topMargin
        || lp.rightMargin != rightMargin || lp.bottomMargin != bottomMargin) {
        lp.leftMargin = leftMargin
        lp.topMargin = topMargin
        lp.rightMargin = rightMargin
        lp.bottomMargin = bottomMargin
        requestLayout()
    }
}

fun DivSize?.toLayoutParamsSize(metrics: DisplayMetrics, resolver: ExpressionResolver): Int {
    return when (this) {
        null -> ViewGroup.LayoutParams.WRAP_CONTENT
        is DivSize.MatchParent -> ViewGroup.LayoutParams.MATCH_PARENT
        is DivSize.WrapContent -> ViewGroup.LayoutParams.WRAP_CONTENT
        is DivSize.Fixed -> value.toPx(metrics, resolver)
    }
}

fun DivFixedSize.toPx(metrics: DisplayMetrics, resolver: ExpressionResolver): Int {
    return when (unit.evaluate(resolver)) {
        DivSizeUnit.DP -> value.evaluate(resolver).dpToPx(metrics)
        DivSizeUnit.SP -> value.evaluate(resolver).spToPx(metrics)
        DivSizeUnit.PX -> value.evaluate(resolver)
    }
}

fun DivFixedSize.toPxF(
    metrics: DisplayMetrics,
    resolver: ExpressionResolver
): Float = evaluatePxFloatByUnit(value.evaluate(resolver), unit.evaluate(resolver), metrics)

fun DivRadialGradientFixedCenter.toPxF(
    metrics: DisplayMetrics,
    resolver: ExpressionResolver
): Float = evaluatePxFloatByUnit(value.evaluate(resolver), unit.evaluate(resolver), metrics)

private fun evaluatePxFloatByUnit(
    value: Int,
    unit: DivSizeUnit,
    metrics: DisplayMetrics
): Float = when (unit) {
    DivSizeUnit.DP -> value.dpToPxF(metrics)
    DivSizeUnit.SP -> value.spToPxF(metrics)
    DivSizeUnit.PX -> value.toFloat()
}

fun DivDimension.toPx(metrics: DisplayMetrics, resolver: ExpressionResolver): Int {
    return when (unit.evaluate(resolver)) {
        DivSizeUnit.DP -> value.evaluate(resolver).dpToPx(metrics)
        DivSizeUnit.SP -> value.evaluate(resolver).spToPx(metrics)
        DivSizeUnit.PX -> value.evaluate(resolver).toInt()
    }
}

internal fun View.applyHeight(div: DivBase, resolver: ExpressionResolver) {
    val height = div.height.toLayoutParamsSize(resources.displayMetrics, resolver)
    if (layoutParams.height != height) {
        ForceParentLayoutParams.setSizeFromChild(this, h = height)
        requestLayout()
    }
    applyTransform(div, resolver)
}

internal fun View.applyWidth(div: DivBase, resolver: ExpressionResolver) {
    val width = div.width.toLayoutParamsSize(resources.displayMetrics, resolver)
    if (layoutParams.width != width) {
        ForceParentLayoutParams.setSizeFromChild(this, w = width)
        requestLayout()
    }
    applyTransform(div, resolver)
}

internal fun View.applyTransform(
    div: DivBase,
    resolver: ExpressionResolver,
) {
    rotation = div.transform.rotation?.evaluate(resolver)?.toFloat() ?: 0f
    if (width == 0 && height == 0) {
        doOnPreDraw {
            pivotX = getPivotValue(width, div.transform.pivotX, resolver)
            pivotY = getPivotValue(height, div.transform.pivotY, resolver)
        }
    } else {
        pivotX = getPivotValue(width, div.transform.pivotX, resolver)
        pivotY = getPivotValue(height, div.transform.pivotY, resolver)
    }
}

private fun getPivotValue(len: Int, divPivot: DivPivot, resolver: ExpressionResolver): Float {
    return when (val pivot = divPivot.value()) {
        is DivPivotFixed -> {
            val offset = pivot.value?.evaluate(resolver)?.toFloat() ?: return len / 2f
            when (pivot.unit.evaluate(resolver)) {
                DivSizeUnit.DP -> dpToPx(offset)
                DivSizeUnit.PX -> offset
                DivSizeUnit.SP -> spToPx(offset)
            }
        }
        is DivPivotPercentage -> pivot.value.evaluate(resolver).toFloat() / 100f * len
        else ->  len / 2f
    }
}

fun View.applyAlpha(alpha: Double) {
    this.alpha = alpha.toFloat()
}

fun View.applyAlignment(
        horizontal: DivAlignmentHorizontal?,
        vertical: DivAlignmentVertical?,
        orientation: DivContainer.Orientation? = null
) {
    (layoutParams as? WrapLayout.LayoutParams)?.let { lp ->
        lp.alignSelf = evaluateAlignSelf(horizontal, vertical, orientation)
    } ?: applyGravity(evaluateGravity(horizontal, vertical))
}

@WrapAlignment
private fun evaluateAlignSelf(
        horizontal: DivAlignmentHorizontal?,
        vertical: DivAlignmentVertical?,
        orientation: DivContainer.Orientation?
) = if (orientation == DivContainer.Orientation.HORIZONTAL)
    vertical.toWrapAlignment(WrapAlignment.AUTO) else horizontal.toWrapAlignment(WrapAlignment.AUTO)

internal fun DivAlignmentHorizontal?.toWrapAlignment(
        @WrapAlignment default: Int = WrapAlignment.START
) = when (this) {
    DivAlignmentHorizontal.LEFT -> WrapAlignment.START
    DivAlignmentHorizontal.CENTER -> WrapAlignment.CENTER
    DivAlignmentHorizontal.RIGHT -> WrapAlignment.END
    else -> default
}

internal fun DivAlignmentVertical?.toWrapAlignment(
        @WrapAlignment default: Int = WrapAlignment.START
) = when (this) {
    DivAlignmentVertical.TOP -> WrapAlignment.START
    DivAlignmentVertical.CENTER -> WrapAlignment.CENTER
    DivAlignmentVertical.BOTTOM -> WrapAlignment.END
    else -> default
}

private fun View.applyGravity(newGravity: Int) {
    when(val lp = layoutParams) {
        is LinearLayout.LayoutParams -> if (lp.gravity != newGravity) {
            lp.gravity = newGravity
            requestLayout()
        }
        is FrameLayout.LayoutParams -> if (lp.gravity != newGravity) {
            lp.gravity = newGravity
            requestLayout()
        }
        is GridContainer.LayoutParams -> if (lp.gravity != newGravity) {
            lp.gravity = newGravity
            requestLayout()
        }
        else -> Log.e("DivView", "tag=$tag: Can't cast $lp to get gravity")
    }
}

fun evaluateGravity(horizontal: DivAlignmentHorizontal?, vertical: DivAlignmentVertical?): Int {
    val horizontalGravity = when (horizontal) {
        DivAlignmentHorizontal.LEFT -> Gravity.LEFT
        DivAlignmentHorizontal.CENTER -> Gravity.CENTER_HORIZONTAL
        DivAlignmentHorizontal.RIGHT -> Gravity.RIGHT
        else -> Gravity.LEFT
    }

    val verticalGravity = when (vertical) {
        DivAlignmentVertical.TOP -> Gravity.TOP
        DivAlignmentVertical.CENTER -> Gravity.CENTER_VERTICAL
        DivAlignmentVertical.BOTTOM -> Gravity.BOTTOM
        else -> Gravity.TOP
    }

    return horizontalGravity or verticalGravity
}

fun Int?.dpToPx(metrics: DisplayMetrics): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this?.toFloat() ?: 0f, metrics).roundToInt()
}

fun Int?.dpToPxF(metrics: DisplayMetrics): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this?.toFloat() ?: 0f, metrics)
}

fun Double?.dpToPx(metrics: DisplayMetrics): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this?.toFloat() ?: 0f, metrics).roundToInt()
}

fun Int?.spToPx(metrics: DisplayMetrics): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this?.toFloat() ?: 0f, metrics).roundToInt()
}

fun Int?.spToPxF(metrics: DisplayMetrics): Float {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this?.toFloat() ?: 0f, metrics)
}

fun Double?.spToPx(metrics: DisplayMetrics): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this?.toFloat() ?: 0f, metrics).roundToInt()
}

fun Int?.unitToPx(metrics: DisplayMetrics, unit: DivSizeUnit): Int {
    return TypedValue.applyDimension(unit.toAndroidUnit(), this?.toFloat() ?: 0f, metrics).roundToInt()
}

internal fun DivImageScale.toScaleType(): ScalingDrawable.ScaleType {
    return when(this) {
        DivImageScale.FILL -> ScalingDrawable.ScaleType.FILL
        DivImageScale.FIT -> ScalingDrawable.ScaleType.FIT
        else -> ScalingDrawable.ScaleType.NO_SCALE
    }
}

fun DivAlignmentHorizontal.toHorizontalAlignment(): ScalingDrawable.AlignmentHorizontal {
    return when(this) {
        DivAlignmentHorizontal.CENTER -> ScalingDrawable.AlignmentHorizontal.CENTER
        DivAlignmentHorizontal.RIGHT -> ScalingDrawable.AlignmentHorizontal.RIGHT
        else -> ScalingDrawable.AlignmentHorizontal.LEFT
    }
}

fun DivAlignmentVertical.toVerticalAlignment(): ScalingDrawable.AlignmentVertical {
    return when(this) {
        DivAlignmentVertical.CENTER -> ScalingDrawable.AlignmentVertical.CENTER
        DivAlignmentVertical.BOTTOM -> ScalingDrawable.AlignmentVertical.BOTTOM
        else -> ScalingDrawable.AlignmentVertical.TOP
    }
}

fun DivRadialGradientRadius.observe(
    resolver: ExpressionResolver,
    subscriber: ExpressionSubscriber,
    callback: (Any) -> Unit
) {
    when (val divRadius = this.value()) {
        is DivFixedSize -> {
            subscriber.addSubscription(divRadius.unit.observe(resolver, callback))
            subscriber.addSubscription(divRadius.value.observe(resolver, callback))
        }
        is DivRadialGradientRelativeRadius -> {
            subscriber.addSubscription(divRadius.value.observe(resolver, callback))
        }
    }
}

fun DivRadialGradientCenter.observe(
    resolver: ExpressionResolver,
    subscriber: ExpressionSubscriber,
    callback: (Any) -> Unit
) {
    when (val divCenter = this.value()) {
        is DivRadialGradientFixedCenter -> {
            subscriber.addSubscription(divCenter.unit.observe(resolver, callback))
            subscriber.addSubscription(divCenter.value.observe(resolver, callback))
        }
        is DivRadialGradientRelativeCenter -> {
            subscriber.addSubscription(divCenter.value.observe(resolver, callback))
        }
    }
}

fun View.applyDivActions(
    divView: Div2View,
    action: DivAction?,
    actions: List<DivAction>?,
    longTapActions: List<DivAction>?,
    doubleTapActions: List<DivAction>?,
    actionAnimation: DivAnimation
) {
    val actionBinder = divView.div2Component.actionBinder
    val tapActions = if (actions.isNullOrEmpty()) {
        action?.let { listOf(it) }
    } else {
        actions
    }
    actionBinder.bindDivActions(divView, this, tapActions, longTapActions, doubleTapActions, actionAnimation)
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

fun TextView.applyLineHeight(lineHeight: Int?, unit: DivSizeUnit) {
    val lineSpacingExtra = lineHeight?.let {
        it.unitToPx(resources.displayMetrics, unit) - this.fontHeight
    } ?: 0
    setLineSpacing(lineSpacingExtra.toFloat(), 1f)
}

fun TextView.applyLetterSpacing(letterSpacing: Double, fontSize: Int) {
    this.letterSpacing = letterSpacing.toFloat() / fontSize
}

fun View.applyId(divId: String?, viewId: Int = View.NO_ID) {
    tag = divId
    id = viewId
}

fun View.applyDescriptionAndHint(contentDescription: String?, hint: String?) {
    this.contentDescription = when {
        contentDescription == null -> hint
        hint == null -> contentDescription
        else -> "$contentDescription\n$hint"
    }
}

internal fun View.applyAccessibilityStateDescription(stateDescription: String?) {
    ViewCompat.setStateDescription(this, stateDescription)
}

internal inline fun <T> Any?.applyIfNotEquals(second: T, applyRef: () -> Unit) {
    if (this != second) {
        applyRef.invoke()
    }
}

internal inline fun <T> List<Any?>.applyIfNotEquals(second: List<T>, applyRef: () -> Unit) {
    if (this.size != second.size) {
        applyRef()

        return
    } else {
        zip(second) { t, s ->
            if (t != s) {
                applyRef()

                return
            }
        }
    }
}

val DivBase.hasVisibilityActions: Boolean
    get() = visibilityAction != null || !visibilityActions.isNullOrEmpty()

val DivBase.allVisibilityActions: List<DivVisibilityAction>
    get() = visibilityActions ?: visibilityAction?.let { listOf(it) }.orEmpty()

fun View.bindLayoutParams(div: DivBase, resolver: ExpressionResolver) = suppressExpressionErrors {
    applyWidth(div, resolver)
    applyHeight(div, resolver)
    applyAlignment(div.alignmentHorizontal?.evaluate(resolver),
        div.alignmentVertical?.evaluate(resolver))
}

internal fun DivImageScale.toImageScale(): AspectImageView.Scale {
    return when(this) {
        DivImageScale.NO_SCALE -> AspectImageView.Scale.NO_SCALE
        DivImageScale.FIT -> AspectImageView.Scale.FIT
        DivImageScale.FILL -> AspectImageView.Scale.FILL
    }
}

@MainThread
fun ViewGroup.trackVisibilityActions(newDivs: List<Div>, oldDivs: List<Div>?, divView: Div2View) {
    val visibilityActionTracker = divView.div2Component.visibilityActionTracker
    if (!oldDivs.isNullOrEmpty()) {
        val newLogIds = newDivs.flatMap {it.value().allVisibilityActions }.mapTo(HashSet()) { it.logId }

        for (oldDiv in oldDivs) {
            val actionsToRemove = oldDiv.value().allVisibilityActions.filter { it.logId !in newLogIds }
            visibilityActionTracker.trackVisibilityActionsOf(divView, null, oldDiv, actionsToRemove)
        }
    }

    if (newDivs.isNotEmpty()) {
        doOnNextLayout {  // Shortcut to check children are laid out at once
            for ((view, div) in children zip newDivs.asSequence()) {
                visibilityActionTracker.trackVisibilityActionsOf(divView, view, div)
            }
        }
    }
}

internal fun getTypeface(fontWeight: DivFontWeight, typefaceProvider: DivTypefaceProvider): Typeface {
    return when (fontWeight) {
        DivFontWeight.LIGHT -> typefaceProvider.light ?: return Typeface.DEFAULT
        DivFontWeight.REGULAR -> typefaceProvider.regular ?: return Typeface.DEFAULT
        DivFontWeight.MEDIUM -> typefaceProvider.medium ?: return Typeface.DEFAULT
        DivFontWeight.BOLD -> typefaceProvider.bold ?: return Typeface.DEFAULT_BOLD
        else -> typefaceProvider.regular ?: return Typeface.DEFAULT
    }
}

internal fun Int.fontSizeToPx(unit: DivSizeUnit, metrics: DisplayMetrics): Float {
    return when (unit) {
        DivSizeUnit.DP -> this.dpToPx(metrics)
        DivSizeUnit.SP -> this.spToPx(metrics)
        DivSizeUnit.PX -> this
    }.toFloat()
}

fun ViewGroup.drawChildrenShadows(canvas: Canvas) {
    for (i in 0 until children.count()) {
        children.elementAt(i).let { child ->
            canvas.withTranslation(child.x, child.y) {
                (child as? DivBorderSupports)?.getDivBorderDrawer()?.drawShadow(canvas)
            }
        }
    }
}

internal fun <T> T.updateBorderDrawer(
        border: DivBorder?,
        resolver: ExpressionResolver
): DivBorderDrawer? where T : DivBorderSupports, T : View {
    val currentDrawer = getDivBorderDrawer()
    if (border == currentDrawer?.border) {
        return currentDrawer
    }

    var newDrawer: DivBorderDrawer? = null
    if (border == null) {
        currentDrawer?.release()

        // Reset outline because it could have been set by DivBorderDrawer.
        clipToOutline = false
        outlineProvider = ViewOutlineProvider.BACKGROUND
    } else if (currentDrawer == null) {
        newDrawer = DivBorderDrawer(resources.displayMetrics, this, resolver, border)
    } else {
        // Try to reuse DivBorderDrawer if possible.
        currentDrawer.setBorder(resolver, border)
        newDrawer = currentDrawer
    }
    invalidate()
    return newDrawer
}
