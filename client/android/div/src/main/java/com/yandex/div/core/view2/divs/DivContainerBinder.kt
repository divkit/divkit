package com.yandex.div.core.view2.divs

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.downloader.DivPatchManager
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.canBeReused
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.util.isBranch
import com.yandex.div.core.util.type
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.divs.widgets.DivFrameLayout
import com.yandex.div.core.view2.divs.widgets.DivLinearLayout
import com.yandex.div.core.view2.divs.widgets.DivWrapLayout
import com.yandex.div.core.view2.divs.widgets.visitViewTree
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.core.widget.AspectView
import com.yandex.div.core.widget.ShowSeparatorsMode
import com.yandex.div.core.widget.wraplayout.WrapDirection
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivMatchParentSize
import com.yandex.div2.DivSize
import javax.inject.Inject
import javax.inject.Provider

private const val INCORRECT_CHILD_SIZE =
    "Incorrect child size. Container with wrap_content size contains child with match_parent size."
private const val INCORRECT_SIZE_ALONG_CROSS_AXIS_MESSAGE = "Incorrect child size. " +
    "Container with wrap layout mode contains child%s with match_parent size along the cross axis."

@DivScope
internal class DivContainerBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val divViewCreator: Provider<DivViewCreator>,
    private val divPatchManager: DivPatchManager,
    private val divPatchCache: DivPatchCache,
    private val divBinder: Provider<DivBinder>,
    private val errorCollectors: ErrorCollectors,
) : DivViewBinder<DivContainer, ViewGroup> {

    override fun bindView(view: ViewGroup, div: DivContainer, divView: Div2View, path: DivStatePath) {
        var oldDiv = when (view) {
            is DivWrapLayout -> view.div
            is DivLinearLayout -> view.div
            is DivFrameLayout -> view.div
            else -> null
        }

        val errorCollector = errorCollectors.getOrCreate(divView.dataTag, divView.divData)

        if (div == oldDiv) {
            // todo MORDAANDROID-636
            // return
        }

        val resolver = divView.expressionResolver

        if (oldDiv != null) baseBinder.unbindExtensions(view, oldDiv, divView)
        val expressionSubscriber = view.expressionSubscriber
        expressionSubscriber.closeAllSubscription()
        baseBinder.bindView(view, div, oldDiv, divView)

        view.observeAspectRatio(resolver, div.aspect)

        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)

        val areDivsReplaceable = DivComparator.areDivsReplaceable(oldDiv, div, resolver)
        when (view) {
            is DivLinearLayout -> view.bindProperties(div, resolver)
            is DivWrapLayout -> view.bindProperties(div, resolver)
            is DivFrameLayout -> view.div = div
        }

        for (childView in view.children) {
            divView.unbindViewFromDiv(childView)
        }
        if (!areDivsReplaceable && oldDiv != null) {
            view.replaceWithReuse(oldDiv, div, divView)
            oldDiv = null
        }
        for (i in div.items.indices) {
            if (div.items[i].value().hasSightActions) {
                divView.bindViewToDiv(view.getChildAt(i), div.items[i])
            }
        }

        var childrenWithIncorrectWidth = 0
        var childrenWithIncorrectHeight = 0

        var viewsPositionDiff = 0
        for (containerIndex in div.items.indices) {
            val childDivValue = div.items[containerIndex].value()
            val childView = view.getChildAt(containerIndex + viewsPositionDiff)
            val childDivId = childDivValue.id

            if (view is DivWrapLayout) {
                div.checkCrossAxisSize(childDivValue, resolver, errorCollector)
            } else {
                if (div.hasIncorrectWidth(childDivValue)) childrenWithIncorrectWidth++
                if (div.hasIncorrectHeight(childDivValue, resolver)) childrenWithIncorrectHeight++
            }

            // applying div patch
            if (childDivId != null) {
                val patchViewsToAdd = divPatchManager.createViewsForId(divView, childDivId)
                val patchDivs = divPatchCache.getPatchDivListById(divView.dataTag, childDivId)
                if (patchViewsToAdd != null && patchDivs != null) {
                    view.removeViewAt(containerIndex + viewsPositionDiff)
                    for (patchIndex in patchViewsToAdd.indices) {
                        val patchDivValue = patchDivs[patchIndex].value()
                        val patchView = patchViewsToAdd[patchIndex]
                        view.addView(patchView, containerIndex + viewsPositionDiff + patchIndex)
                        observeChildViewAlignment(div, patchDivValue, patchView, resolver, expressionSubscriber)
                        if (patchDivValue.hasSightActions) {
                            divView.bindViewToDiv(patchView, patchDivs[patchIndex])
                        }
                    }
                    viewsPositionDiff += patchViewsToAdd.size - 1
                    continue
                }
            }

            divBinder.get().bind(childView, div.items[containerIndex], divView, path)
            observeChildViewAlignment(div, childDivValue, childView, resolver, expressionSubscriber)
        }

        view.trackVisibilityActions(div.items, oldDiv?.items, divView)

        val widthAllChildrenAreIncorrect = childrenWithIncorrectWidth == div.items.size
        val widthHasMatchParentChild = childrenWithIncorrectWidth > 0
        val heightAllChildrenAreIncorrect = childrenWithIncorrectHeight == div.items.size
        val heightHasMatchParentChild = childrenWithIncorrectHeight > 0

        val hasIncorrectSize = !div.isWrapContainer(resolver) && when {
            div.isVertical(resolver) -> widthAllChildrenAreIncorrect || heightHasMatchParentChild
            div.isHorizontal(resolver) -> heightAllChildrenAreIncorrect || widthHasMatchParentChild
            else -> widthAllChildrenAreIncorrect || heightAllChildrenAreIncorrect
        }

        if (hasIncorrectSize) {
            addIncorrectChildSizeWarning(errorCollector)
        }
    }

    private fun ViewGroup.replaceWithReuse(oldDiv: DivContainer, newDiv: DivContainer, divView: Div2View) {
        val resolver = divView.expressionResolver

        val oldChildren = mutableMapOf<Div, View>()
        oldDiv.items.zip(children.toList()) { childDiv, child ->
            oldChildren[childDiv] = child
        }

        removeAllViews()

        val createViewIndices = mutableListOf<Int>()

        newDiv.items.forEachIndexed { index, newChild ->
            val oldViewIndex = oldChildren.keys.firstOrNull { oldChildDiv ->
                if (oldChildDiv.isBranch) {
                    newChild.type == oldChildDiv.type
                } else {
                    oldChildDiv.canBeReused(newChild, resolver)
                }
            }

            val childView = oldChildren.remove(oldViewIndex)

            if (childView != null) {
                addView(childView)
            } else {
                createViewIndices += index
            }
        }

        createViewIndices.forEach { index ->
            val newChildDiv = newDiv.items[index]

            val oldViewIndex = oldChildren.keys.firstOrNull { oldChildDiv ->
                oldChildDiv.type == newChildDiv.type
            }

            val childView = oldChildren
                .remove(oldViewIndex)
                ?: divViewCreator.get().create(newChildDiv, divView.expressionResolver)

            addView(childView, index)
        }

        oldChildren.values.forEach {
            divView.releaseViewVisitor.visitViewTree(it)
        }
    }

    private fun DivLinearLayout.bindProperties(div: DivContainer, resolver: ExpressionResolver) {
        addSubscription(div.orientation.observeAndGet(resolver) {
            orientation = if (div.isHorizontal(resolver)) {
                LinearLayoutCompat.HORIZONTAL
            } else {
                LinearLayoutCompat.VERTICAL
            }
        })
        observeContentAlignment(div, resolver) { gravity = it }
        div.separator?.let { observeSeparator(it, resolver) }

        this.div = div
    }

    private fun ExpressionSubscriber.observeContentAlignment(
        div: DivContainer,
        resolver: ExpressionResolver,
        applyGravity: (Int) -> Unit
    ) {
        addSubscription(div.contentAlignmentHorizontal.observeAndGet(resolver) {
            applyGravity(evaluateGravity(it, div.contentAlignmentVertical.evaluate(resolver)))
        })
        addSubscription(div.contentAlignmentVertical.observeAndGet(resolver) {
            applyGravity(evaluateGravity(div.contentAlignmentHorizontal.evaluate(resolver), it))
        })
    }

    private fun DivLinearLayout.observeSeparator(
        separator: DivContainer.Separator,
        resolver: ExpressionResolver
    ) {
        observeSeparatorShowMode(separator, resolver) { showDividers = it }
        observeSeparatorDrawable(this, separator, resolver) { dividerDrawable = it }
        observeSeparatorMargins(this, separator.margins, resolver) {
            left, top, right, bottom -> setDividerMargins(left, top, right, bottom)
        }
    }

    private fun DivWrapLayout.bindProperties(div: DivContainer, resolver: ExpressionResolver) {
        addSubscription(div.orientation.observeAndGet(resolver) {
            wrapDirection =
                if (div.isHorizontal(resolver)) WrapDirection.ROW else WrapDirection.COLUMN
        })
        observeContentAlignment(div, resolver) { gravity = it }

        div.separator?.let { separator ->
            observeSeparatorShowMode(separator, resolver) { showSeparators = it }
            observeSeparatorDrawable(this, separator, resolver) { separatorDrawable = it }
            observeSeparatorMargins(this, separator.margins, resolver)  {
                left, top, right, bottom -> setSeparatorMargins(left, top, right, bottom)
            }
        }
        div.lineSeparator?.let { separator ->
            observeSeparatorShowMode(separator, resolver) { showLineSeparators = it }
            observeSeparatorDrawable(this, separator, resolver) { lineSeparatorDrawable = it }
            observeSeparatorMargins(this, separator.margins, resolver) {
                left, top, right, bottom -> setLineSeparatorMargins(left, top, right, bottom)
            }
        }

        this.div = div
    }

    private fun ExpressionSubscriber.observeSeparatorShowMode(
        separator: DivContainer.Separator,
        resolver: ExpressionResolver,
        applySeparatorShowMode: (Int) -> Unit
    ) {
        val callback = { _: Any ->
            var showSeparators = ShowSeparatorsMode.NONE
            if (separator.showAtStart.evaluate(resolver)) {
                showSeparators = showSeparators or ShowSeparatorsMode.SHOW_AT_START
            }
            if (separator.showBetween.evaluate(resolver)) {
                showSeparators = showSeparators or ShowSeparatorsMode.SHOW_BETWEEN
            }
            if (separator.showAtEnd.evaluate(resolver)) {
                showSeparators = showSeparators or ShowSeparatorsMode.SHOW_AT_END
            }
            applySeparatorShowMode(showSeparators)
        }
        addSubscription(separator.showAtStart.observe(resolver, callback))
        addSubscription(separator.showBetween.observe(resolver, callback))
        addSubscription(separator.showAtEnd.observe(resolver, callback))
        callback(Unit)
    }

    private fun ExpressionSubscriber.observeSeparatorDrawable(
        view: ViewGroup,
        separator: DivContainer.Separator,
        resolver: ExpressionResolver,
        applyDrawable: (Drawable?) -> Unit
    ) = observeDrawable(resolver, separator.style) {
        applyDrawable(it.toDrawable(view.resources.displayMetrics, resolver))
    }

    private fun ExpressionSubscriber.observeSeparatorMargins(
        view: View,
        margins: DivEdgeInsets,
        resolver: ExpressionResolver,
        applyMargins: (left: Int, top: Int, right: Int, bottom: Int) -> Unit
    ) {
        val metrics = view.resources.displayMetrics
        val callback = { _: Any? ->
            val sizeUnit = margins.unit.evaluate(resolver)

            val left = margins.left.evaluate(resolver).unitToPx(metrics, sizeUnit)
            val right = margins.right.evaluate(resolver).unitToPx(metrics, sizeUnit)
            val top = margins.top.evaluate(resolver).unitToPx(metrics, sizeUnit)
            val bottom = margins.bottom.evaluate(resolver).unitToPx(metrics, sizeUnit)

            applyMargins(left, top, right, bottom)
        }

        callback(null)
        addSubscription(margins.unit.observe(resolver, callback))
        addSubscription(margins.left.observe(resolver, callback))
        addSubscription(margins.top.observe(resolver, callback))
        addSubscription(margins.right.observe(resolver, callback))
        addSubscription(margins.bottom.observe(resolver, callback))
    }

    private fun observeChildViewAlignment(
        div: DivContainer,
        childDivValue: DivBase,
        childView: View,
        resolver: ExpressionResolver,
        expressionSubscriber: ExpressionSubscriber,
    ) {
        val applyAlignments = { _: Any ->
            val childAlignmentHorizontal = childDivValue.alignmentHorizontal
            val alignmentHorizontal = when {
                childAlignmentHorizontal != null -> childAlignmentHorizontal.evaluate(resolver)
                div.isWrapContainer(resolver) -> null
                else -> div.contentAlignmentHorizontal.evaluate(resolver).toAlignmentHorizontal()
            }

            val childAlignmentVertical = childDivValue.alignmentVertical
            val alignmentVertical = when {
                childAlignmentVertical != null -> childAlignmentVertical.evaluate(resolver)
                div.isWrapContainer(resolver) -> null
                else -> div.contentAlignmentVertical.evaluate(resolver).toAlignmentVertical()
            }

            childView.applyAlignment(alignmentHorizontal, alignmentVertical)
        }

        expressionSubscriber.addSubscription(
            div.contentAlignmentHorizontal.observe(resolver, applyAlignments)
        )
        expressionSubscriber.addSubscription(
            div.contentAlignmentVertical.observe(resolver, applyAlignments)
        )
        expressionSubscriber.addSubscription(
            div.orientation.observe(resolver, applyAlignments)
        )

        applyAlignments(childView)
    }

    private fun DivContainer.checkCrossAxisSize(
        childDiv: DivBase,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector
    ) = if (isHorizontal(resolver)) {
        childDiv.height.checkForCrossAxis(childDiv, errorCollector)
    } else {
        childDiv.width.checkForCrossAxis(childDiv, errorCollector)
    }

    private fun DivSize.checkForCrossAxis(childDiv: DivBase, errorCollector: ErrorCollector) {
        if (value() is DivMatchParentSize) {
            addIncorrectSizeALongCrossAxisWarning(errorCollector, childDiv.id)
        }
    }

    private fun DivContainer.hasIncorrectWidth(childDiv: DivBase) =
        width is DivSize.WrapContent && childDiv.width is DivSize.MatchParent

    private fun DivContainer.hasIncorrectHeight(childDiv: DivBase, resolver: ExpressionResolver) =
        height is DivSize.WrapContent
            && aspect?.let { it.ratio.evaluate(resolver).toFloat() == AspectView.DEFAULT_ASPECT_RATIO } ?: true
            && childDiv.height is DivSize.MatchParent

    private fun addIncorrectChildSizeWarning(errorCollector: ErrorCollector) {
        errorCollector.getWarnings().forEach {
            if (it.message == INCORRECT_CHILD_SIZE) return
        }
        errorCollector.logWarning(Throwable(INCORRECT_CHILD_SIZE))
    }

    private fun addIncorrectSizeALongCrossAxisWarning(errorCollector: ErrorCollector, childId: String?) {
        val withId = childId?.let { " with id='$it'" } ?: ""
        errorCollector.logWarning(Throwable(
            INCORRECT_SIZE_ALONG_CROSS_AXIS_MESSAGE.format(withId)))
    }

    fun setDataWithoutBinding(view: ViewGroup, div: DivContainer) {
        when (view) {
            is DivWrapLayout -> view.div = div
            is DivLinearLayout -> view.div = div
            is DivFrameLayout -> view.div = div
        }
        for (containerIndex in div.items.indices) {
            divBinder.get().setDataWithoutBinding(
                view.getChildAt(containerIndex),
                div.items[containerIndex]
            )
        }
    }
}
