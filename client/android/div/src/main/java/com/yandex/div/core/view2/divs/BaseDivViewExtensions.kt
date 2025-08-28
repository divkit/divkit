@file:JvmMultifileClass
@file:JvmName("BaseDivViewExtensionsKt")

package com.yandex.div.core.view2.divs

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.annotation.MainThread
import androidx.core.graphics.scale
import androidx.core.graphics.withSave
import androidx.core.view.children
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import com.yandex.div.core.util.allAppearActions
import com.yandex.div.core.util.allDisappearActions
import com.yandex.div.core.util.allSightActions
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.util.evaluateGravity
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
import com.yandex.div.core.view2.reuse.InputFocusTracker
import com.yandex.div.core.widget.AspectView
import com.yandex.div.internal.Log
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.core.getItemResolver
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstant
import com.yandex.div.json.expressions.isConstantOrNull
import com.yandex.div2.DivAction
import com.yandex.div2.DivAlignmentHorizontal
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivAnimation
import com.yandex.div2.DivAspect
import com.yandex.div2.DivBase
import com.yandex.div2.DivCollectionItemBuilder
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivFilter
import com.yandex.div2.DivPivot
import com.yandex.div2.DivPivotFixed
import com.yandex.div2.DivPivotPercentage
import com.yandex.div2.DivSizeUnit
import kotlin.math.max

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

internal fun View.applyTransform(
    div: DivBase,
    resolver: ExpressionResolver,
) {
    val transform = div.transform
    transform?.rotation?.evaluate(resolver)?.toFloat()?.let {
        rotation = it
    } ?: run {
        rotation = 0.0f
        return
    }

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

private fun View.applyBaselineAlignment(baselineAligned: Boolean) {
    val lp = layoutParams as? DivLayoutParams ?: return
    if (lp.isBaselineAligned != baselineAligned) {
        lp.isBaselineAligned = baselineAligned
        requestLayout()
    }
}

internal fun View.applyDivActions(
    context: BindingContext,
    action: DivAction?,
    actions: List<DivAction>?,
    longTapActions: List<DivAction>?,
    doubleTapActions: List<DivAction>?,
    hoverStartActions: List<DivAction>?,
    hoverEndActions: List<DivAction>?,
    pressStartActions: List<DivAction>?,
    pressEndActions: List<DivAction>?,
    actionAnimation: DivAnimation,
    captureFocusOnAction: Expression<Boolean>,
) {
    val actionBinder = context.divView.div2Component.actionBinder
    val tapActions = if (actions.isNullOrEmpty()) {
        action?.let { listOf(it) }
    } else {
        actions
    }
    actionBinder.bindDivActions(
        context,
        this,
        tapActions,
        longTapActions,
        doubleTapActions,
        hoverStartActions,
        hoverEndActions,
        pressStartActions,
        pressEndActions,
        actionAnimation,
        captureFocusOnAction,
    )
}

internal fun View.createAnimatedTouchListener(
    context: BindingContext,
    divAnimation: DivAnimation?,
    divGestureListener: DivGestureListener?
): ((View, MotionEvent) -> Boolean)? {
    val animations = divAnimation?.asTouchListener(context.expressionResolver, this)

    // Avoid creating GestureDetector if unnecessary cause it's expensive.
    val gestureDetector = divGestureListener
        ?.takeUnless { it.onSingleTapListener == null && it.onDoubleTapListener == null }
        ?.let { GestureDetector(context.divView.context, divGestureListener, Handler(Looper.getMainLooper())) }

    return if (animations != null || gestureDetector != null) {
        { v, event ->
            animations?.invoke(v, event)
            gestureDetector?.onTouchEvent(event) ?: false
        }
    } else {
        null
    }
}

/**
 * Binds all descendants of [this] which are [DivStateLayout]s corresponding to DivStates in [div]
 */
internal fun View.bindStates(bindingContext: BindingContext, binder: DivBinder) {
    traverseViewHierarchy(this) { currentView ->
        if (currentView !is DivStateLayout) return@traverseViewHierarchy true
        val div = currentView.div ?: return@traverseViewHierarchy false
        val path = currentView.path ?: return@traverseViewHierarchy false
        binder.bind(bindingContext, currentView, div, path.parentState())
        false
    }
}

private fun traverseViewHierarchy(view: View, action: (View) -> Boolean) {
    if (!action(view) || view !is ViewGroup) {
        return
    }
    view.children.forEach {
        traverseViewHierarchy(it, action)
    }
}

@MainThread
internal fun ViewGroup.trackVisibilityActions(
    divView: Div2View,
    newItems: List<DivItemBuilderResult>,
    oldItems: List<DivItemBuilderResult>?,
) {
    val visibilityActionTracker = divView.div2Component.visibilityActionTracker
    if (!oldItems.isNullOrEmpty()) {
        val newLogIds = newItems.flatMap { it.div.value().allSightActions }.mapTo(HashSet()) { it.logId }

        for (oldItem in oldItems) {
            val appearActionsToRemove = oldItem.div.value().allAppearActions.filter { it.logId !in newLogIds }
            val disappearActionsToRemove = oldItem.div.value().allDisappearActions.filter { it.logId !in newLogIds }
            visibilityActionTracker.trackVisibilityActionsOf(
                divView,
                oldItem.expressionResolver,
                null,
                oldItem.div,
                appearActionsToRemove,
                disappearActionsToRemove
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

internal fun View.drawShadow(canvas: Canvas) {
    canvas.withSave {
        translate(x, y)
        rotate(rotation, pivotX, pivotY)
        (this@drawShadow as? DivBorderSupports)?.getDivBorderDrawer()?.drawShadow(canvas)
    }
}

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

private fun AspectView.applyAspectRatio(ratio: Double?) {
    aspectRatio = ratio?.toFloat() ?: AspectView.DEFAULT_ASPECT_RATIO
}

internal fun View.applyBitmapFilters(
    context: BindingContext,
    bitmap: Bitmap,
    filters: List<DivFilter>?,
    actionAfterFilters: (Bitmap) -> Unit
) {
    if (filters.isNullOrEmpty()) {
        actionAfterFilters(bitmap)
        return
    }

    val resolver = context.expressionResolver
    val bitmapEffectHelper = context.divView.div2Component.bitmapEffectHelper

    doOnActualLayout {
        val scale = max(height / bitmap.height.toFloat(), width / bitmap.width.toFloat())
        var result = bitmap.scale((scale * bitmap.width).toInt(), (scale * bitmap.height).toInt(), false)
        for (filter in filters) {
            when (filter) {
                is DivFilter.Blur -> {
                    val radius = filter.value.radius.evaluate(resolver).toIntSafely().dpToPx(resources.displayMetrics)
                    result = bitmapEffectHelper.blurBitmap(result, radius.toFloat())
                }

                is DivFilter.RtlMirror -> if (isLayoutRtl()) {
                    result = bitmapEffectHelper.mirrorBitmap(result)
                }
            }
        }
        actionAfterFilters(result)
    }
}

internal fun View.clearFocusOnClick(focusTracker: InputFocusTracker) {
    if (this.isFocused || !this.isInTouchMode) return
    focusTracker.removeFocusFromFocusedInput()
}

internal val View.bindingContext get() = (this as? DivHolderView<*>)?.bindingContext

internal fun bindItemBuilder(builder: DivCollectionItemBuilder, resolver: ExpressionResolver, callback: (Any) -> Unit) {
    builder.data.observe(resolver, callback)

    val itemResolver = builder.getItemResolver(resolver)
    builder.prototypes.forEach {
        it.selector.observe(itemResolver, callback)
    }
}

internal fun View.gainAccessibilityFocus() {
    performAccessibilityAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS, null)
    sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED)
}

internal fun ViewGroup.bindClipChildren(
    newClipToBounds: Expression<Boolean>,
    oldClipToBounds: Expression<Boolean>?,
    resolver: ExpressionResolver
) {
    if (newClipToBounds.equalsToConstant(oldClipToBounds)) {
        return
    }

    applyClipChildren(newClipToBounds.evaluate(resolver))

    if (newClipToBounds.isConstant()) {
        return
    }

    (this as? DivHolderView<*>)?.addSubscription(
        newClipToBounds.observe(resolver) { clip -> applyClipChildren(clip) }
    )
}

private fun ViewGroup.applyClipChildren(clip: Boolean) {
    (this as? DivHolderView<*>)?.needClipping = clip
    val parent = parent
    if (!clip && parent is ViewGroup) {
        parent.clipChildren = false
    }
}
