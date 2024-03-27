package com.yandex.div.core.view2.divs

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.MainThread
import androidx.core.graphics.withTranslation
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.children
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import com.yandex.div.core.expression.suppressExpressionErrors
import com.yandex.div.core.font.DivTypefaceProvider
import com.yandex.div.core.state.DivPathUtils.findDivState
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.util.isLayoutRtl
import com.yandex.div.core.util.toIntSafely
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivGestureListener
import com.yandex.div.core.view2.animations.asTouchListener
import com.yandex.div.core.view2.divs.widgets.DivBorderSupports
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.core.widget.AspectView
import com.yandex.div.core.widget.FixedLineHeightView
import com.yandex.div.core.widget.FixedLineHeightView.Companion.UNDEFINED_LINE_HEIGHT
import com.yandex.div.internal.Log
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.drawable.CircleDrawable
import com.yandex.div.internal.drawable.RoundedRectDrawable
import com.yandex.div.internal.drawable.ScalingDrawable
import com.yandex.div.internal.widget.AspectImageView
import com.yandex.div.internal.widget.DivGravity
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.internal.widget.indicator.IndicatorParams
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstantOrNull
import com.yandex.div2.Div
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAction
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivAnimation
import com.yandex.div2.DivAspect
import com.yandex.div2.DivBase
import com.yandex.div2.DivBlendMode
import com.yandex.div2.DivBorder
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContentAlignmentHorizontal
import com.yandex.div2.DivContentAlignmentVertical
import com.yandex.div2.DivDefaultIndicatorItemPlacement
import com.yandex.div2.DivDimension
import com.yandex.div2.DivDisappearAction
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivFilter
import com.yandex.div2.DivFixedSize
import com.yandex.div2.DivFontWeight
import com.yandex.div2.DivImageScale
import com.yandex.div2.DivIndicator
import com.yandex.div2.DivIndicatorItemPlacement
import com.yandex.div2.DivPivot
import com.yandex.div2.DivPivotFixed
import com.yandex.div2.DivPivotPercentage
import com.yandex.div2.DivRadialGradientFixedCenter
import com.yandex.div2.DivShape
import com.yandex.div2.DivShapeDrawable
import com.yandex.div2.DivSightAction
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivStroke
import com.yandex.div2.DivTransform
import com.yandex.div2.DivVisibilityAction
import com.yandex.div2.DivWrapContentSize
import kotlin.math.max
import kotlin.math.roundToInt

internal fun View.applyPaddings(insets: DivEdgeInsets?, resolver: ExpressionResolver) {
    if (insets == null) {
        setPadding(0, 0, 0, 0)
        return
    }

    val metrics = resources.displayMetrics
    val unit = insets.unit.evaluate(resolver)
    if (insets.start != null || insets.end != null) {
        setPaddingRelative(
            insets.start?.evaluate(resolver)?.toPx(unit, metrics) ?: 0,
            insets.top.evaluate(resolver).toPx(unit, metrics),
            insets.end?.evaluate(resolver)?.toPx(unit, metrics) ?: 0,
            insets.bottom.evaluate(resolver).toPx(unit, metrics)
        )
    } else {
        setPadding(
            insets.left.evaluate(resolver).toPx(unit, metrics),
            insets.top.evaluate(resolver).toPx(unit, metrics),
            insets.right.evaluate(resolver).toPx(unit, metrics),
            insets.bottom.evaluate(resolver).toPx(unit, metrics))
    }
}

internal fun View.applyMargins(insets: DivEdgeInsets?, resolver: ExpressionResolver) {
    val metrics = resources.displayMetrics
    val lp = layoutParams as? ViewGroup.MarginLayoutParams ?: return

    var leftMargin = 0
    var topMargin = 0
    var rightMargin = 0
    var bottomMargin = 0
    var startMargin : Int? = null
    var endMargin : Int? = null

    if (insets != null) {
        val unit = insets.unit.evaluate(resolver)
        leftMargin = insets.left.evaluate(resolver).unitToPx(metrics, unit)
        topMargin = insets.top.evaluate(resolver).unitToPx(metrics, unit)
        rightMargin = insets.right.evaluate(resolver).unitToPx(metrics, unit)
        bottomMargin = insets.bottom.evaluate(resolver).unitToPx(metrics, unit)
        insets.start?.let {
            startMargin = it.evaluate(resolver).unitToPx(metrics, unit)
        }
        insets.end?.let {
            endMargin = it.evaluate(resolver).unitToPx(metrics, unit)
        }
    }

    if (lp.leftMargin != leftMargin || lp.topMargin != topMargin
        || lp.rightMargin != rightMargin || lp.bottomMargin != bottomMargin
        || (startMargin != null && lp.marginStart != startMargin)
        || (endMargin != null && lp.marginEnd != endMargin)) {
        lp.topMargin = topMargin
        lp.bottomMargin = bottomMargin
        if (startMargin != null || endMargin != null) {
            lp.marginStart = startMargin ?: 0
            lp.marginEnd = endMargin ?: 0
        } else {
            lp.leftMargin = leftMargin
            lp.rightMargin = rightMargin
        }
        requestLayout()
    }
}

internal fun DivSize?.toLayoutParamsSize(
    metrics: DisplayMetrics,
    resolver: ExpressionResolver,
    lp: ViewGroup.LayoutParams? = null
): Int {
    return when (this) {
        null -> ViewGroup.LayoutParams.WRAP_CONTENT
        is DivSize.MatchParent -> ViewGroup.LayoutParams.MATCH_PARENT
        is DivSize.Fixed -> value.toPx(metrics, resolver)
        is DivSize.WrapContent -> when {
            value.constrained?.evaluate(resolver) != true -> ViewGroup.LayoutParams.WRAP_CONTENT
            lp is DivLayoutParams -> DivLayoutParams.WRAP_CONTENT_CONSTRAINED
            else -> ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }
}

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

internal fun DivWrapContentSize.ConstraintSize.toPx(metrics: DisplayMetrics, resolver: ExpressionResolver): Int {
    return when (unit.evaluate(resolver)) {
        DivSizeUnit.DP -> value.evaluate(resolver).dpToPx(metrics)
        DivSizeUnit.SP -> value.evaluate(resolver).spToPx(metrics)
        DivSizeUnit.PX -> value.evaluate(resolver).toIntSafely()
    }
}

internal fun DivFixedSize.toPxF(
    metrics: DisplayMetrics,
    resolver: ExpressionResolver
): Float = evaluatePxFloatByUnit(value.evaluate(resolver), unit.evaluate(resolver), metrics)

internal fun DivRadialGradientFixedCenter.toPxF(
    metrics: DisplayMetrics,
    resolver: ExpressionResolver
): Float = evaluatePxFloatByUnit(value.evaluate(resolver), unit.evaluate(resolver), metrics)

private fun evaluatePxFloatByUnit(
    value: Long,
    unit: DivSizeUnit,
    metrics: DisplayMetrics
): Float = when (unit) {
    DivSizeUnit.DP -> value.dpToPxF(metrics)
    DivSizeUnit.SP -> value.spToPxF(metrics)
    DivSizeUnit.PX -> value.toFloat()
}

internal fun DivDimension.toPx(metrics: DisplayMetrics, resolver: ExpressionResolver): Int {
    return when (unit.evaluate(resolver)) {
        DivSizeUnit.DP -> value.evaluate(resolver).dpToPx(metrics)
        DivSizeUnit.SP -> value.evaluate(resolver).spToPx(metrics)
        DivSizeUnit.PX -> value.evaluate(resolver).toInt()
    }
}

internal fun View.applyHeight(div: DivBase, resolver: ExpressionResolver) {
    val height = div.height.toLayoutParamsSize(resources.displayMetrics, resolver, layoutParams)
    if (layoutParams.height != height) {
        layoutParams.height = height
        requestLayout()
    }
    applyTransform(div.transform, resolver)
}

internal fun View.applyMinHeight(minHeight: DivWrapContentSize.ConstraintSize?, resolver: ExpressionResolver) {
    val heightValue = minHeight?.toPx(resources.displayMetrics, resolver) ?: 0
    if (minimumHeight != heightValue) {
        minimumHeight = heightValue
        requestLayout()
    }
}

internal fun View.applyVerticalWeightValue(value: Float) {
    val params = layoutParams as? DivLayoutParams ?: return
    if (params.verticalWeight != value) {
        params.verticalWeight = value
        requestLayout()
    }
}

internal fun View.applyMaxHeight(maxHeight: DivWrapContentSize.ConstraintSize?, resolver: ExpressionResolver) {
    val params = layoutParams as? DivLayoutParams ?: return
    val heightValue = maxHeight?.toPx(resources.displayMetrics, resolver) ?: Int.MAX_VALUE
    if (params.maxHeight != heightValue) {
        params.maxHeight = heightValue
        requestLayout()
    }
}

internal fun View.applyWidth(div: DivBase, resolver: ExpressionResolver) {
    val width = div.width.toLayoutParamsSize(resources.displayMetrics, resolver, layoutParams)
    if (layoutParams.width != width) {
        layoutParams.width = width
        requestLayout()
    }
    applyTransform(div.transform, resolver)
}

internal fun View.applyMinWidth(minWidth: DivWrapContentSize.ConstraintSize?, resolver: ExpressionResolver) {
    val widthValue = minWidth?.toPx(resources.displayMetrics, resolver) ?: 0
    if (minimumWidth != widthValue) {
        minimumWidth = widthValue
        requestLayout()
    }
}

internal fun View.applyHorizontalWeightValue(value: Float) {
    val params = layoutParams as? DivLayoutParams ?: return
    if (params.horizontalWeight != value) {
        params.horizontalWeight = value
        requestLayout()
    }
}

internal fun DivSize.getWeight(resolver: ExpressionResolver) = when (this) {
    is DivSize.MatchParent -> value.weight?.evaluate(resolver)?.toFloat() ?: DivLayoutParams.DEFAULT_WEIGHT
    else -> DivLayoutParams.DEFAULT_WEIGHT
}

internal fun View.applyMaxWidth(maxWidth: DivWrapContentSize.ConstraintSize?, resolver: ExpressionResolver) {
    val params = layoutParams as? DivLayoutParams ?: return
    val widthValue = maxWidth?.toPx(resources.displayMetrics, resolver) ?: Int.MAX_VALUE
    if (params.maxWidth != widthValue) {
        params.maxWidth = widthValue
        requestLayout()
    }
}

internal fun View.applyTransform(
    transform: DivTransform?,
    resolver: ExpressionResolver,
) {
    val rotation = transform?.rotation?.evaluate(resolver)?.toFloat()
    if (rotation == null) {
        this.rotation = 0.0f
        return
    }
    this.rotation = rotation

    if (width == 0 && height == 0) {
        doOnPreDraw {
            pivotX = getPivotValue(width, transform.pivotX, resolver)
            pivotY = getPivotValue(height, transform.pivotY, resolver)
        }
    } else {
        pivotX = getPivotValue(width, transform.pivotX, resolver)
        pivotY = getPivotValue(height, transform.pivotY, resolver)
    }
}

private fun View.getPivotValue(len: Int, divPivot: DivPivot, resolver: ExpressionResolver): Float {
    return when (val pivot = divPivot.value()) {
        is DivPivotFixed -> {
            val offset = pivot.value?.evaluate(resolver)?.toFloat() ?: return len / 2f
            when (pivot.unit.evaluate(resolver)) {
                DivSizeUnit.DP -> offset.dpToPxF(resources.displayMetrics)
                DivSizeUnit.PX -> offset
                DivSizeUnit.SP -> offset.spToPxF(resources.displayMetrics)
            }
        }
        is DivPivotPercentage -> pivot.value.evaluate(resolver).toFloat() / 100f * len
        else ->  len / 2f
    }
}

internal fun View.applyAlpha(alpha: Double) {
    this.alpha = alpha.toFloat()
}

internal fun DivContainer.isHorizontal(resolver: ExpressionResolver) =
    orientation.evaluate(resolver) == DivContainer.Orientation.HORIZONTAL

internal fun DivContainer.isVertical(resolver: ExpressionResolver) =
    orientation.evaluate(resolver) == DivContainer.Orientation.VERTICAL

internal fun DivContainer.isWrapContainer(resolver: ExpressionResolver) = when {
    layoutMode.evaluate(resolver) != DivContainer.LayoutMode.WRAP -> false
    orientation.evaluate(resolver) == DivContainer.Orientation.OVERLAP -> false
    isHorizontal(resolver) -> width.canWrap(resolver)
    height.canWrap(resolver) -> true
    else -> aspect?.let { it.ratio.evaluate(resolver).toFloat() != AspectView.DEFAULT_ASPECT_RATIO } ?: false
}

internal fun DivSize.canWrap(resolver: ExpressionResolver) =
    this !is DivSize.WrapContent || value.constrained?.evaluate(resolver) == true

internal fun View.applyAlignment(
    horizontal: DivAlignmentHorizontal?,
    vertical: DivAlignmentVertical?
) {
    applyGravity(evaluateGravity(horizontal, vertical))
    applyBaselineAlignment(vertical == DivAlignmentVertical.BASELINE)
}

private fun View.applyGravity(newGravity: Int) {
    when (val lp = layoutParams) {
        is DivLayoutParams -> if (lp.gravity != newGravity) {
            lp.gravity = newGravity
            requestLayout()
        }
        else -> Log.e("DivView", "tag=$tag: Can't cast $lp to get gravity")
    }
}

internal fun evaluateGravity(horizontal: DivAlignmentHorizontal?, vertical: DivAlignmentVertical?): Int {
    val horizontalGravity = when (horizontal) {
        DivAlignmentHorizontal.LEFT -> Gravity.LEFT
        DivAlignmentHorizontal.CENTER -> Gravity.CENTER_HORIZONTAL
        DivAlignmentHorizontal.RIGHT -> Gravity.RIGHT
        DivAlignmentHorizontal.START -> Gravity.START
        DivAlignmentHorizontal.END -> Gravity.END
        else -> Gravity.START
    }

    val verticalGravity = when (vertical) {
        DivAlignmentVertical.TOP -> Gravity.TOP
        DivAlignmentVertical.CENTER -> Gravity.CENTER_VERTICAL
        DivAlignmentVertical.BOTTOM -> Gravity.BOTTOM
        else -> Gravity.TOP
    }

    return horizontalGravity or verticalGravity
}

internal fun evaluateGravity(horizontal: DivContentAlignmentHorizontal?, vertical: DivContentAlignmentVertical?): Int {
    val horizontalGravity = when (horizontal) {
        DivContentAlignmentHorizontal.LEFT -> Gravity.LEFT
        DivContentAlignmentHorizontal.CENTER -> Gravity.CENTER_HORIZONTAL
        DivContentAlignmentHorizontal.RIGHT -> Gravity.RIGHT
        DivContentAlignmentHorizontal.START -> Gravity.START
        DivContentAlignmentHorizontal.END -> Gravity.END
        DivContentAlignmentHorizontal.SPACE_AROUND -> DivGravity.SPACE_AROUND_HORIZONTAL
        DivContentAlignmentHorizontal.SPACE_BETWEEN -> DivGravity.SPACE_BETWEEN_HORIZONTAL
        DivContentAlignmentHorizontal.SPACE_EVENLY -> DivGravity.SPACE_EVENLY_HORIZONTAL
        else -> Gravity.START
    }

    val verticalGravity = when (vertical) {
        DivContentAlignmentVertical.TOP -> Gravity.TOP
        DivContentAlignmentVertical.CENTER -> Gravity.CENTER_VERTICAL
        DivContentAlignmentVertical.BOTTOM -> Gravity.BOTTOM
        DivContentAlignmentVertical.SPACE_AROUND -> DivGravity.SPACE_AROUND_VERTICAL
        DivContentAlignmentVertical.SPACE_BETWEEN -> DivGravity.SPACE_BETWEEN_VERTICAL
        DivContentAlignmentVertical.SPACE_EVENLY -> DivGravity.SPACE_EVENLY_VERTICAL
        else -> Gravity.TOP
    }

    return horizontalGravity or verticalGravity
}

private fun View.applyBaselineAlignment(baselineAligned: Boolean) {
    val lp = layoutParams as? DivLayoutParams ?: return
    if (lp.isBaselineAligned != baselineAligned) {
        lp.isBaselineAligned = baselineAligned
        requestLayout()
    }
}

fun <T : Number> T?.dpToPxF(metrics: DisplayMetrics): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this?.toFloat() ?: 0f, metrics)

fun <T : Number> T?.spToPxF(metrics: DisplayMetrics): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, this?.toFloat() ?: 0f, metrics)

fun <T : Number> T?.unitToPxF(metrics: DisplayMetrics, unit: DivSizeUnit): Float =
    TypedValue.applyDimension(unit.toAndroidUnit(), this?.toFloat() ?: 0f, metrics)

fun <T : Number> T?.pxToDpF(metrics: DisplayMetrics): Float =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        TypedValue.deriveDimension(TypedValue.COMPLEX_UNIT_DIP, this?.toFloat() ?: 0f, metrics)
    } else {
        (this?.toFloat() ?: 0f) / metrics.density
    }

fun <T : Number> T?.pxToSpF(metrics: DisplayMetrics): Float =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        TypedValue.deriveDimension(TypedValue.COMPLEX_UNIT_SP, this?.toFloat() ?: 0f, metrics)
    } else {
        (this?.toFloat() ?: 0f) / metrics.scaledDensity
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

internal fun DivImageScale.toScaleType(): ScalingDrawable.ScaleType {
    return when(this) {
        DivImageScale.FILL -> ScalingDrawable.ScaleType.FILL
        DivImageScale.FIT -> ScalingDrawable.ScaleType.FIT
        DivImageScale.STRETCH -> ScalingDrawable.ScaleType.STRETCH
        else -> ScalingDrawable.ScaleType.NO_SCALE
    }
}

internal fun DivAlignmentHorizontal.toHorizontalAlignment(): ScalingDrawable.AlignmentHorizontal {
    return when(this) {
        DivAlignmentHorizontal.CENTER -> ScalingDrawable.AlignmentHorizontal.CENTER
        DivAlignmentHorizontal.RIGHT -> ScalingDrawable.AlignmentHorizontal.RIGHT
        else -> ScalingDrawable.AlignmentHorizontal.LEFT
    }
}

internal fun DivAlignmentVertical.toVerticalAlignment(): ScalingDrawable.AlignmentVertical {
    return when(this) {
        DivAlignmentVertical.CENTER -> ScalingDrawable.AlignmentVertical.CENTER
        DivAlignmentVertical.BOTTOM -> ScalingDrawable.AlignmentVertical.BOTTOM
        else -> ScalingDrawable.AlignmentVertical.TOP
    }
}

internal fun DivBlendMode.toPorterDuffMode(): PorterDuff.Mode {
    return when (this) {
        DivBlendMode.SOURCE_IN -> PorterDuff.Mode.SRC_IN
        DivBlendMode.SOURCE_ATOP -> PorterDuff.Mode.SRC_ATOP
        DivBlendMode.DARKEN -> PorterDuff.Mode.DARKEN
        DivBlendMode.LIGHTEN -> PorterDuff.Mode.LIGHTEN
        DivBlendMode.MULTIPLY -> PorterDuff.Mode.MULTIPLY
        DivBlendMode.SCREEN -> PorterDuff.Mode.SCREEN
    }
}

internal fun View.applyDivActions(
    context: BindingContext,
    action: DivAction?,
    actions: List<DivAction>?,
    longTapActions: List<DivAction>?,
    doubleTapActions: List<DivAction>?,
    actionAnimation: DivAnimation,
    accessibility: DivAccessibility?,
) {
    val actionBinder = context.divView.div2Component.actionBinder
    val tapActions = if (actions.isNullOrEmpty()) {
        action?.let { listOf(it) }
    } else {
        actions
    }
    actionBinder.bindDivActions(context, this, tapActions, longTapActions, doubleTapActions,
        actionAnimation, accessibility)
}

internal fun View.setAnimatedTouchListener(
    context: BindingContext,
    divAnimation: DivAnimation?,
    divGestureListener: DivGestureListener?
) {
    val animations = divAnimation?.asTouchListener(context.expressionResolver, this)

    // Avoid creating GestureDetector if unnecessary cause it's expensive.
    val gestureDetector = divGestureListener
        ?.takeUnless { it.onSingleTapListener == null && it.onDoubleTapListener == null }
        ?.let { GestureDetectorCompat(context.divView.context, divGestureListener) }

    if (animations != null || gestureDetector != null) {
        //noinspection ClickableViewAccessibility
        setOnTouchListener { v, event ->
            animations?.invoke(v, event)
            gestureDetector?.onTouchEvent(event) ?: false
        }
    } else {
        setOnTouchListener(null)
    }
}

internal fun TextView.applyFontSize(fontSize: Int, unit: DivSizeUnit) {
    setTextSize(unit.toAndroidUnit(), fontSize.toFloat())
}

internal fun DivSizeUnit.toAndroidUnit(): Int {
    return when (this) {
        DivSizeUnit.DP -> TypedValue.COMPLEX_UNIT_DIP
        DivSizeUnit.SP -> TypedValue.COMPLEX_UNIT_SP
        DivSizeUnit.PX -> TypedValue.COMPLEX_UNIT_PX
    }
}

internal fun <T> T.applyLineHeight(
    lineHeight: Long?,
    unit: DivSizeUnit
) where T : TextView, T : FixedLineHeightView {
    fixedLineHeight = lineHeight?.unitToPx(resources.displayMetrics, unit) ?: UNDEFINED_LINE_HEIGHT
}

internal fun TextView.applyLetterSpacing(letterSpacing: Double, fontSize: Int) {
    this.letterSpacing = letterSpacing.toFloat() / fontSize
}

internal fun View.applyId(divId: String?, viewId: Int = View.NO_ID) {
    tag = divId
    id = viewId
}

internal val DivBase.hasSightActions: Boolean
    get() = visibilityAction != null || !visibilityActions.isNullOrEmpty() || !disappearActions.isNullOrEmpty()

internal val DivBase.allVisibilityActions: List<DivVisibilityAction>
    get() = visibilityActions ?: visibilityAction?.let { listOf(it) }.orEmpty()

internal val DivBase.allDisappearActions: List<DivDisappearAction>
    get() = disappearActions.orEmpty()

internal val DivBase.allSightActions: List<DivSightAction>
    get() = this.allDisappearActions + this.allVisibilityActions

internal fun View.bindLayoutParams(div: DivBase, resolver: ExpressionResolver) = suppressExpressionErrors {
    applyWidth(div, resolver)
    applyHeight(div, resolver)
    applyAlignment(div.alignmentHorizontal?.evaluate(resolver),
        div.alignmentVertical?.evaluate(resolver))
}

/**
 * Binds all descendants of [this] which are [DivStateLayout]s corresponding to DivStates in [div]
 */
internal fun View.bindStates(
    div: Div?,
    context: BindingContext,
    resolver: ExpressionResolver,
    binder: DivBinder,
) {
    if (div == null) return
    val statesByPath = mutableMapOf<DivStatePath, DivStateLayout>()
    traverseViewHierarhy(this) { currentView ->
        if (currentView !is DivStateLayout) {
            return@traverseViewHierarhy true
        }
        currentView.path?.let { path ->
            statesByPath[path] = currentView
        }
        false
    }
    statesByPath.entries.forEach { (path, layout) ->
        val divByPath = div.findDivState(path, resolver)
        if (divByPath != null) {
            val parentState = path.parentState()
            binder.bind(context, layout, divByPath, parentState)
        }
    }
}

private fun traverseViewHierarhy(view: View, action: (View) -> Boolean) {
    if (!action(view) || view !is ViewGroup) {
        return
    }
    view.children.forEach {
        traverseViewHierarhy(it, action)
    }
}

internal fun DivImageScale.toImageScale(): AspectImageView.Scale {
    return when(this) {
        DivImageScale.NO_SCALE -> AspectImageView.Scale.NO_SCALE
        DivImageScale.FIT -> AspectImageView.Scale.FIT
        DivImageScale.FILL -> AspectImageView.Scale.FILL
        DivImageScale.STRETCH -> AspectImageView.Scale.STRETCH
    }
}

@MainThread
internal fun ViewGroup.trackVisibilityActions(
    newItems: List<DivItemBuilderResult>,
    oldItems: List<DivItemBuilderResult>?,
    divView: Div2View
) {
    val visibilityActionTracker = divView.div2Component.visibilityActionTracker
    if (!oldItems.isNullOrEmpty()) {
        val newLogIds = newItems.flatMap { it.div.value().allSightActions }.mapTo(HashSet()) { it.logId }

        for (oldDiv in oldItems) {
            val actionsToRemove = oldDiv.div.value().allSightActions.filter { it.logId !in newLogIds }
            visibilityActionTracker.trackVisibilityActionsOf(
                divView,
                oldDiv.expressionResolver,
                null,
                oldDiv.div,
                actionsToRemove
            )
        }
    }

    if (newItems.isNotEmpty()) {
        doOnNextLayout {  // Shortcut to check children are laid out at once
            for ((view, item) in children zip newItems.asSequence()) {
                visibilityActionTracker.trackVisibilityActionsOf(
                    divView,
                    item.expressionResolver,
                    view,
                    item.div
                )
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

internal fun Long.fontSizeToPx(unit: DivSizeUnit, metrics: DisplayMetrics): Float {
    return when (unit) {
        DivSizeUnit.DP -> this.dpToPx(metrics)
        DivSizeUnit.SP -> this.spToPx(metrics)
        DivSizeUnit.PX -> this
    }.toFloat()
}

internal fun ViewGroup.drawChildrenShadows(canvas: Canvas) {
    for (i in 0 until children.count()) {
        children.elementAt(i).let { child ->
            canvas.withTranslation(child.x, child.y) {
                (child as? DivBorderSupports)?.getDivBorderDrawer()?.drawShadow(canvas)
            }
        }
    }
}

internal fun View.extractParentContentAlignmentVertical(
    resolver: ExpressionResolver
): DivContentAlignmentVertical? {
    val div = (parent as? DivHolderView<*>)?.div
    return (div as? DivContainer)?.contentAlignmentVertical?.evaluate(resolver)
}

internal fun View.extractParentContentAlignmentHorizontal(
    resolver: ExpressionResolver
): DivContentAlignmentHorizontal? {
    val div = (parent as? DivHolderView<*>)?.div
    return (div as? DivContainer)?.contentAlignmentHorizontal?.evaluate(resolver)
}

internal fun DivBorder?.isConstantlyEmpty(): Boolean {
    this ?: return true
    if (cornerRadius != null) return false
    if (cornersRadius != null) return false
    if (hasShadow != Expression.constant(false)) return false
    if (shadow != null) return false
    return stroke == null
}

internal fun DivDrawable.toDrawable(
    metrics: DisplayMetrics,
    resolver: ExpressionResolver
): Drawable? {
    return when (this) {
        is DivDrawable.Shape -> value.toDrawable(metrics, resolver)
    }
}

internal fun DivShapeDrawable.toDrawable(
    metrics: DisplayMetrics,
    resolver: ExpressionResolver
): Drawable? {
    return when (val shape = this.shape) {
        is DivShape.RoundedRectangle -> {
            RoundedRectDrawable(
                RoundedRectDrawable.Params(
                    width = shape.value.itemWidth.toPxF(metrics, resolver),
                    height = shape.value.itemHeight.toPxF(metrics, resolver),
                    color = (shape.value.backgroundColor ?: color).evaluate(resolver),
                    radius = shape.value.cornerRadius.toPxF(metrics, resolver),
                    strokeColor = (shape.value.stroke ?: stroke)?.color?.evaluate(resolver),
                    strokeWidth = (shape.value.stroke ?: stroke)?.getWidthPxF(metrics, resolver)
                )
            )
        }
        is DivShape.Circle ->
            CircleDrawable(
                CircleDrawable.Params(
                    radius = shape.value.radius.toPxF(metrics, resolver),
                    color = (shape.value.backgroundColor ?: color).evaluate(resolver),
                    strokeColor = (shape.value.stroke ?: stroke)?.color?.evaluate(resolver),
                    strokeWidth = (shape.value.stroke ?: stroke)?.getWidthPxF(metrics, resolver)
                )
            )
        else -> null
    }
}

private fun DivStroke.getWidthPxF(metrics: DisplayMetrics, resolver: ExpressionResolver) =
    width.evaluate(resolver).unitToPxF(metrics, unit.evaluate(resolver))

internal val DivIndicator.itemsPlacementCompat : DivIndicatorItemPlacement get() {
    return itemsPlacement ?: DivIndicatorItemPlacement.Default(
        DivDefaultIndicatorItemPlacement(
            spaceBetweenCenters = spaceBetweenCenters
        )
    )
}

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

internal fun View.bindAspectRatio(newAspect: DivAspect?, oldAspect: DivAspect?, resolver: ExpressionResolver) {
    if (this !is AspectView) {
        return
    }

    if (newAspect?.ratio.equalsToConstant(oldAspect?.ratio)) {
        return
    }

    applyAspectRatio(newAspect?.ratio?.evaluate(resolver))

    if (newAspect?.ratio.isConstantOrNull() || this !is ExpressionSubscriber) {
        return
    }

    addSubscription(newAspect?.ratio?.observe(resolver) { ratio -> applyAspectRatio(ratio) })
}

internal fun View.applyBitmapFilters(
    context: BindingContext,
    bitmap: Bitmap,
    filters: List<DivFilter>?,
    actionAfterFilters: (Bitmap) -> Unit
) {
    if (filters == null) {
        actionAfterFilters(bitmap)
        return
    }

    val resolver = context.expressionResolver
    val bitmapEffectHelper = context.divView.div2Component.bitmapEffectHelper

    doOnActualLayout {
        val scale = max(height / bitmap.height.toFloat(), width / bitmap.width.toFloat())
        var result = Bitmap.createScaledBitmap(
            /* src = */ bitmap,
            /* dstWidth = */ (scale * bitmap.width).toInt(),
            /* dstHeight = */ (scale * bitmap.height).toInt(),
            /* filter = */ false
        )
        for (filter in filters) {
            when (filter) {
                is DivFilter.Blur -> {
                    val radius = filter.value.radius.evaluate(resolver).toIntSafely().dpToPx(resources.displayMetrics)
                    result = bitmapEffectHelper.blurBitmap(result, radius)
                }

                is DivFilter.RtlMirror -> if (isLayoutRtl()) {
                    result = bitmapEffectHelper.mirrorBitmap(result)
                }
            }
        }
        actionAfterFilters(result)
    }
}

private fun AspectView.applyAspectRatio(ratio: Double?) {
    aspectRatio = ratio?.toFloat() ?: AspectView.DEFAULT_ASPECT_RATIO
}

internal fun DivContentAlignmentHorizontal.toAlignmentHorizontal(): DivAlignmentHorizontal = when (this) {
    DivContentAlignmentHorizontal.LEFT -> DivAlignmentHorizontal.LEFT
    DivContentAlignmentHorizontal.CENTER -> DivAlignmentHorizontal.CENTER
    DivContentAlignmentHorizontal.RIGHT -> DivAlignmentHorizontal.RIGHT
    else -> DivAlignmentHorizontal.LEFT
}

internal fun DivContentAlignmentVertical.toAlignmentVertical(): DivAlignmentVertical = when (this) {
    DivContentAlignmentVertical.TOP -> DivAlignmentVertical.TOP
    DivContentAlignmentVertical.CENTER -> DivAlignmentVertical.CENTER
    DivContentAlignmentVertical.BOTTOM -> DivAlignmentVertical.BOTTOM
    DivContentAlignmentVertical.BASELINE -> DivAlignmentVertical.BASELINE
    else -> DivAlignmentVertical.TOP
}
