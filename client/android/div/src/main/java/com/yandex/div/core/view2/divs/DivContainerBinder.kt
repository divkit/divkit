package com.yandex.div.core.view2.divs

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.children
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.downloader.DivPatchManager
import com.yandex.div.core.expression.ExpressionSubscriber
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
import com.yandex.div.core.widget.LinearContainerLayout
import com.yandex.div.core.widget.wraplayout.WrapDirection
import com.yandex.div.core.widget.wraplayout.WrapShowSeparatorsMode
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivAlignmentVertical
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivMatchParentSize
import com.yandex.div2.DivSize
import javax.inject.Inject
import javax.inject.Provider

private const val INCORRECT_CHILD_SIZE = "Incorrect child size. Container with wrap_content size contains child%s with match_parent size."
private const val MATCH_PARENT_ALONG_CROSS_AXIS_MESSAGE = "Incorrect child size. " +
    "Container with wrap layout mode contains child%s with match parent size along the cross axis."

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
            if (div.items[i].value().hasVisibilityActions) {
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

            if (view is DivWrapLayout &&
                div.hasMatchParentAlongCrossAxis(childDivValue, resolver)) {
                addWarning(errorCollector, MATCH_PARENT_ALONG_CROSS_AXIS_MESSAGE, false, childDivValue.id)
            } else {
                if (div.hasIncorrectWidth(childDivValue)) childrenWithIncorrectWidth++
                if (div.hasIncorrectHeight(childDivValue)) childrenWithIncorrectHeight++
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
                        if (patchDivValue.hasVisibilityActions) {
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
            addWarning(errorCollector, INCORRECT_CHILD_SIZE)
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
        addSubscription(div.contentAlignmentHorizontal.observeAndGet(resolver) {
            gravity = evaluateGravity(it, div.contentAlignmentVertical.evaluate(resolver))
        })
        addSubscription(div.contentAlignmentVertical.observeAndGet(resolver) {
            isBaselineAligned = it == DivAlignmentVertical.BASELINE
            gravity = evaluateGravity(div.contentAlignmentHorizontal.evaluate(resolver), it)
        })
        div.separator?.let { observeSeparator(it, resolver) }

        this.div = div
    }

    private fun DivLinearLayout.observeSeparator(
        separator: DivContainer.Separator,
        resolver: ExpressionResolver
    ) {
        observeSeparatorShowMode(separator, resolver) {
            var showSeparators = LinearLayoutCompat.SHOW_DIVIDER_NONE
            if (separator.showAtStart.evaluate(resolver)) {
                showSeparators = showSeparators or LinearLayoutCompat.SHOW_DIVIDER_BEGINNING
            }
            if (separator.showBetween.evaluate(resolver)) {
                showSeparators = showSeparators or LinearLayoutCompat.SHOW_DIVIDER_MIDDLE
            }
            if (separator.showAtEnd.evaluate(resolver)) {
                showSeparators = showSeparators or LinearLayoutCompat.SHOW_DIVIDER_END
            }
            showDividers = showSeparators
        }
        observeSeparatorDrawable(this, separator, resolver) { dividerDrawable = it }
    }

    private fun DivWrapLayout.bindProperties(div: DivContainer, resolver: ExpressionResolver) {
        addSubscription(div.orientation.observeAndGet(resolver) {
            wrapDirection =
                if (div.isHorizontal(resolver)) WrapDirection.ROW else WrapDirection.COLUMN
        })
        addSubscription(div.contentAlignmentHorizontal.observeAndGet(resolver) {
            alignmentHorizontal = it.toWrapAlignment()
        })
        addSubscription(div.contentAlignmentVertical.observeAndGet(resolver) {
            alignmentVertical = it.toWrapAlignment()
        })

        div.separator?.let { separator ->
            observeSeparatorShowMode(separator, resolver) {
                showSeparators = getWrapShowSeparatorsMode(separator, resolver)
            }
            observeSeparatorDrawable(this, separator, resolver) { separatorDrawable = it }
        }
        div.lineSeparator?.let { separator ->
            observeSeparatorShowMode(separator, resolver) {
                showLineSeparators = getWrapShowSeparatorsMode(separator, resolver)
            }
            observeSeparatorDrawable(this, separator, resolver) { lineSeparatorDrawable = it }
        }

        this.div = div
    }

    private fun ExpressionSubscriber.observeSeparatorShowMode(
        separator: DivContainer.Separator,
        resolver: ExpressionResolver,
        callback: (Boolean) -> Unit
    ) {
        callback(false)
        addSubscription(separator.showAtStart.observe(resolver, callback))
        addSubscription(separator.showBetween.observe(resolver, callback))
        addSubscription(separator.showAtEnd.observe(resolver, callback))
    }

    private fun ExpressionSubscriber.observeSeparatorDrawable(
        view: ViewGroup,
        separator: DivContainer.Separator,
        resolver: ExpressionResolver,
        applyDrawable: (Drawable?) -> Unit
    ) = observeDrawable(resolver, separator.style) {
        applyDrawable(it.toDrawable(view.resources.displayMetrics, resolver))
    }

    @WrapShowSeparatorsMode
    private fun getWrapShowSeparatorsMode(
        separator: DivContainer.Separator,
        resolver: ExpressionResolver
    ): Int {
        var showSeparators = WrapShowSeparatorsMode.NONE
        if (separator.showAtStart.evaluate(resolver)) {
            showSeparators = showSeparators or WrapShowSeparatorsMode.SHOW_AT_START
        }
        if (separator.showBetween.evaluate(resolver)) {
            showSeparators = showSeparators or WrapShowSeparatorsMode.SHOW_BETWEEN
        }
        if (separator.showAtEnd.evaluate(resolver)) {
            showSeparators = showSeparators or WrapShowSeparatorsMode.SHOW_AT_END
        }
        return showSeparators
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
                childAlignmentHorizontal != null -> childAlignmentHorizontal
                div.isWrapContainer(resolver) -> null
                else -> div.contentAlignmentHorizontal
            }

            val childAlignmentVertical = childDivValue.alignmentVertical
            val alignmentVertical = when {
                childAlignmentVertical != null -> childAlignmentVertical
                div.isWrapContainer(resolver) -> null
                else -> div.contentAlignmentVertical
            }

            childView.applyAlignment(alignmentHorizontal?.evaluate(resolver),
                alignmentVertical?.evaluate(resolver), div.orientation.evaluate(resolver))

            if (div.isVertical(resolver) && childDivValue.height is DivSize.MatchParent) {
                if (!div.isWrapContainer(resolver)) {
                    ForceParentLayoutParams.setSizeFromParent(childView, h = 0)
                }
                childView.applyWeight(childDivValue.height.value() as DivMatchParentSize, resolver)
            } else if (div.isHorizontal(resolver) && childDivValue.width is DivSize.MatchParent) {
                if (!div.isWrapContainer(resolver)) {
                    ForceParentLayoutParams.setSizeFromParent(childView, w = 0)
                }
                childView.applyWeight(childDivValue.width.value() as DivMatchParentSize, resolver)
            }
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

        if ((div.isVertical(resolver) && childDivValue.height is DivSize.MatchParent)) {
            (childDivValue.height.value() as DivMatchParentSize).weight?.let {
                expressionSubscriber.addSubscription(
                    it.observe(resolver, applyAlignments)
                )
            }
        } else if(div.isHorizontal(resolver) && childDivValue.width is DivSize.MatchParent) {
            (childDivValue.width.value() as DivMatchParentSize).weight?.let {
                expressionSubscriber.addSubscription(
                    it.observe(resolver, applyAlignments)
                )
            }
        }

        applyAlignments(childView)
    }

    private fun DivContainer.hasMatchParentAlongCrossAxis(
        childDiv: DivBase,
        resolver: ExpressionResolver
    ) = if (isHorizontal(resolver)) {
        childDiv.height is DivSize.MatchParent
    } else {
        childDiv.width is DivSize.MatchParent
    }

    private fun DivContainer.hasIncorrectWidth(childDiv: DivBase) = width is DivSize.WrapContent && childDiv.width is DivSize.MatchParent

    private fun DivContainer.hasIncorrectHeight(childDiv: DivBase) = height is DivSize.WrapContent && childDiv.height is DivSize.MatchParent

    private fun addWarning(
        errorCollector: ErrorCollector,
        warningMessage: String,
        checkDuplicates: Boolean = true,
        childId: String? = null) {
        if (checkDuplicates) {
            errorCollector.getWarnings().forEach {
                if (it.message == warningMessage) return
            }
        }
        val withId = childId?.let { " with id='$it'" } ?: ""
        errorCollector.logWarning(Throwable(
            warningMessage.format(withId)))
    }

    private fun View.applyWeight(size: DivMatchParentSize, resolver: ExpressionResolver) {
        val params = layoutParams
        if (params is LinearContainerLayout.LayoutParams) {
            params.weight = size.weight?.evaluate(resolver)?.toFloat() ?: 1.0f
            requestLayout()
        }
    }
}
