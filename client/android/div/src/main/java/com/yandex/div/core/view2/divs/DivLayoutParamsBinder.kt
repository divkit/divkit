package com.yandex.div.core.view2.divs

import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup.LayoutParams
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.equalsToConstant
import com.yandex.div.core.util.isConstant
import com.yandex.div.core.util.observeSize
import com.yandex.div.core.util.toLayoutParamsSize
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.widget.DivLayoutParams
import com.yandex.div.internal.widget.DivLayoutParams.Companion.DEFAULT_MAX_SIZE
import com.yandex.div.internal.widget.DivLayoutParams.Companion.DEFAULT_MIN_SIZE
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivSize
import com.yandex.div2.DivSizeUnit
import com.yandex.div2.DivSizeUnitValue
import javax.inject.Inject

private const val INCORRECT_CONSTRAINTS_MESSAGE =
    "Element has incorrect %s constraints (min size is bigger than max size). %sminSize: %s, maxSize: %s."
private const val ELEMENT_ID = "Id: '%s', "
private const val WIDTH = "width"
private const val HEIGHT = "height"

@DivScope
internal class DivLayoutParamsBinder @Inject constructor(
    private val errorCollectors: ErrorCollectors,
) {

    fun bindLayoutParams(
        context: BindingContext,
        target: View,
        newDiv: DivBase,
        oldDiv: DivBase?,
        subscriber: ExpressionSubscriber,
    ) {
        val errorCollector = errorCollectors.getOrCreate(context.divView.dataTag, context.divView.divData)
        target.bindLayoutParams(newDiv, oldDiv, context.expressionResolver, subscriber, errorCollector)
    }

    private fun View.bindLayoutParams(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber,
        errorCollector: ErrorCollector,
    ) {
        if (layoutParams == null) {
            KAssert.fail { "LayoutParams should be initialized before view binding" }
            layoutParams = DivLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        }

        bindWidth(newDiv, oldDiv, resolver, subscriber, errorCollector)
        bindHeight(newDiv, oldDiv, resolver, subscriber, errorCollector)
    }

    private fun View.bindWidth(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber,
        errorCollector: ErrorCollector,
    ) {
        if (newDiv.width.equalsToConstant(oldDiv?.width)) {
            newDiv.width.checkConstraints(WIDTH, newDiv.id, resources.displayMetrics, resolver, errorCollector)
            return
        }

        applyWidthProperties(newDiv, resolver, errorCollector)

        if (newDiv.width.isConstant()) return

        subscriber.observeSize(newDiv.width, resolver) {
            applyWidthProperties(newDiv, resolver, errorCollector)
        }
    }

    private fun DivSize.checkConstraints(
        type: String,
        id: String?,
        metrics: DisplayMetrics,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
    ) {
        val minSize = minSize ?: return
        val maxSize = maxSize ?: return
        val minPx = minSize.toPx(metrics, resolver)
        val maxPx = maxSize.toPx(metrics, resolver)
        if (minPx <= maxPx) return

        val elementId = id?.let { ELEMENT_ID.format(it) } ?: ""
        val minValue = minSize.toString(resolver)
        val maxValue = maxSize.toString(resolver)
        val message = INCORRECT_CONSTRAINTS_MESSAGE.format(type, elementId, minValue, maxValue)
        errorCollector.logError(Throwable(message))
    }

    private fun DivSizeUnitValue.toString(resolver: ExpressionResolver) =
        "${value.evaluate(resolver)}${DivSizeUnit.toString(unit.evaluate(resolver))}"

    private fun View.applyWidthProperties(
        div: DivBase,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
    ) {
        div.width.checkConstraints(WIDTH, div.id, resources.displayMetrics, resolver, errorCollector)

        var changed = false
        changed = applyWidth(div, resolver) || changed
        changed = applyHorizontalWeightValue(div, resolver) || changed
        changed = applyWidthConstraints(div, resolver) || changed
        if (changed) {
            requestLayout()
            applyTransform(div, resolver)
        }
    }

    private fun View.applyWidth(div: DivBase, resolver: ExpressionResolver): Boolean {
        val width = div.width.toLayoutParamsSize(resources.displayMetrics, resolver, layoutParams)
        if (layoutParams.width == width) return false

        layoutParams.width = width
        return true
    }

    private fun View.applyHorizontalWeightValue(div: DivBase, resolver: ExpressionResolver): Boolean {
        val params = layoutParams as? DivLayoutParams ?: return false
        val value = div.width.getWeight(resolver)
        if (params.horizontalWeight == value) return false

        params.horizontalWeight = value
        return true
    }

    private fun View.applyWidthConstraints(div: DivBase, resolver: ExpressionResolver): Boolean {
        var minSize = div.width.minSize?.toPx(resources.displayMetrics, resolver) ?: DEFAULT_MIN_SIZE
        var maxSize = div.width.maxSize?.toPx(resources.displayMetrics, resolver) ?: DEFAULT_MAX_SIZE
        if (minSize > maxSize) {
            minSize = DEFAULT_MIN_SIZE
            maxSize = DEFAULT_MAX_SIZE
        }

        var changed = false
        if (minimumWidth != minSize) {
            minimumWidth = minSize
            changed = true
        }

        val params = layoutParams as? DivLayoutParams ?: return changed
        if (params.maxWidth != maxSize) {
            params.maxWidth = maxSize
            changed = true
        }

        return changed
    }

    private fun DivSize.getWeight(resolver: ExpressionResolver) =
        (this as? DivSize.MatchParent)?.value?.weight?.evaluate(resolver)?.toFloat() ?: DivLayoutParams.DEFAULT_WEIGHT

    private fun View.bindHeight(
        newDiv: DivBase,
        oldDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber,
        errorCollector: ErrorCollector,
    ) {

        if (newDiv.height.equalsToConstant(oldDiv?.height)) {
            newDiv.height.checkConstraints(HEIGHT, newDiv.id, resources.displayMetrics, resolver, errorCollector)
            return
        }

        applyHeightProperties(newDiv, resolver, errorCollector)

        if (newDiv.height.isConstant()) return

        subscriber.observeSize(newDiv.height, resolver) {
            applyHeightProperties(newDiv, resolver, errorCollector)
        }
    }

    private fun View.applyHeightProperties(
        div: DivBase,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
    ) {
        div.height.checkConstraints(HEIGHT, div.id, resources.displayMetrics, resolver, errorCollector)

        var changed = false
        changed = applyHeight(div, resolver) || changed
        changed = applyVerticalWeightValue(div, resolver) || changed
        changed = applyHeightConstraints(div, resolver) || changed
        if (changed) {
            requestLayout()
            applyTransform(div, resolver)
        }
    }

    private fun View.applyHeight(div: DivBase, resolver: ExpressionResolver): Boolean {
        val height = div.height.toLayoutParamsSize(resources.displayMetrics, resolver, layoutParams)
        if (layoutParams.height == height) return false

        layoutParams.height = height
        return true
    }

    private fun View.applyVerticalWeightValue(div: DivBase, resolver: ExpressionResolver): Boolean {
        val params = layoutParams as? DivLayoutParams ?: return false
        val value = div.height.getWeight(resolver)
        if (params.verticalWeight == value) return false

        params.verticalWeight = value
        return true
    }

    private fun View.applyHeightConstraints(div: DivBase, resolver: ExpressionResolver): Boolean {
        var minSize = div.height.minSize?.toPx(resources.displayMetrics, resolver) ?: DEFAULT_MIN_SIZE
        var maxSize = div.height.maxSize?.toPx(resources.displayMetrics, resolver) ?: DEFAULT_MAX_SIZE
        if (minSize > maxSize) {
            minSize = DEFAULT_MIN_SIZE
            maxSize = DEFAULT_MAX_SIZE
        }

        var changed = false
        if (minimumHeight != minSize) {
            minimumHeight = minSize
            changed = true
        }

        val params = layoutParams as? DivLayoutParams ?: return changed
        if (params.maxHeight != maxSize) {
            params.maxHeight = maxSize
            changed = true
        }

        return changed
    }

    private val DivSize.minSize: DivSizeUnitValue? get() {
        return when (this) {
            is DivSize.WrapContent -> value.minSize
            is DivSize.MatchParent -> value.minSize
            else -> null
        }
    }

    private val DivSize.maxSize: DivSizeUnitValue? get() {
        return when (this) {
            is DivSize.WrapContent -> value.maxSize
            is DivSize.MatchParent -> value.maxSize
            else -> null
        }
    }
}
