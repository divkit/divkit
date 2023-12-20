package com.yandex.div.core.view2.divs

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import com.yandex.div.core.Disposable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.downloader.DivPatchCache
import com.yandex.div.core.downloader.DivPatchManager
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.canBeReused
import com.yandex.div.core.util.equalsToConstant
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.util.isBranch
import com.yandex.div.core.util.isConstant
import com.yandex.div.core.util.observeDrawable
import com.yandex.div.core.util.type
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.core.view2.divs.widgets.DivLinearLayout
import com.yandex.div.core.view2.divs.widgets.DivWrapLayout
import com.yandex.div.core.view2.divs.widgets.visitViewTree
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.core.widget.AspectView
import com.yandex.div.core.widget.ShowSeparatorsMode
import com.yandex.div.core.widget.wraplayout.WrapDirection
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.core.buildItems
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstant
import com.yandex.div.json.expressions.isConstantOrNull
import com.yandex.div2.Div
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivContentAlignmentHorizontal
import com.yandex.div2.DivContentAlignmentVertical
import com.yandex.div2.DivDrawable
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivMatchParentSize
import com.yandex.div2.DivSize
import javax.inject.Inject
import javax.inject.Provider

private const val INCORRECT_CHILD_SIZE =
    "Incorrect child size. Container with wrap_content size contains child with match_parent size."
private const val INCORRECT_SIZE_ALONG_CROSS_AXIS_MESSAGE = "Incorrect child size. " +
    "Container with wrap layout mode contains child%s with match_parent size along the cross axis."

private const val NO_PATCH_SHIFT = -2

@DivScope
internal class DivContainerBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val divViewCreator: Provider<DivViewCreator>,
    private val divPatchManager: DivPatchManager,
    private val divPatchCache: DivPatchCache,
    private val divBinder: Provider<DivBinder>,
    private val errorCollectors: ErrorCollectors,
) : DivViewBinder<DivContainer, ViewGroup> {

    private val tempRect = Rect()

    override fun bindView(view: ViewGroup, div: DivContainer, divView: Div2View, path: DivStatePath) {
        @Suppress("UNCHECKED_CAST")
        val divHolderView = view as DivHolderView<DivContainer>
        val oldDiv = divHolderView.div

        baseBinder.bindView(view, div, oldDiv, divView)
        view.applyDivActions(divView, div.action, div.actions, div.longtapActions, div.doubletapActions, div.actionAnimation)

        val resolver = divView.expressionResolver
        val subscriber = divView.expressionSubscriber
        val errorCollector = errorCollectors.getOrCreate(divView.dataTag, divView.divData)

        view.observeAspectRatio(resolver, div.aspect)

        when (view) {
            is DivLinearLayout -> view.bindProperties(div, oldDiv, resolver)
            is DivWrapLayout -> view.bindProperties(div, oldDiv, resolver)
        }

        view.bindClipChildren(div, oldDiv, resolver)

        for (childView in view.children) {
            divView.unbindViewFromDiv(childView)
        }

        val items = div.buildItems(resolver)
        val oldItems = oldDiv?.buildItems(resolver)?.let {
            when {
                div === oldDiv -> it
                DivComparator.areValuesReplaceable(oldDiv, div, resolver) &&
                    DivComparator.areChildrenReplaceable(it, items, resolver) -> it

                else -> {
                    view.replaceWithReuse(it, items, divView)
                    null
                }
            }
        }

        view.validateChildren(div, resolver, errorCollector)
        view.dispatchBinding(divView, div, oldDiv, path, resolver, subscriber)

        items.forEachIndexed { i, item ->
            if (item.value().hasSightActions) {
                divView.bindViewToDiv(view.getChildAt(i), item)
            }
        }
        view.trackVisibilityActions(items, oldItems, divView)
    }

    private fun ViewGroup.validateChildren(
        div: DivContainer,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector
    ) {
        val items = div.buildItems(resolver)
        var childrenWithIncorrectWidth = 0
        var childrenWithIncorrectHeight = 0

        items.forEach { item ->
            val childDivValue = item.value()

            if (this is DivWrapLayout) {
                div.checkCrossAxisSize(childDivValue, resolver, errorCollector)
            } else {
                if (div.hasIncorrectWidth(childDivValue)) childrenWithIncorrectWidth++
                if (div.hasIncorrectHeight(childDivValue, resolver)) childrenWithIncorrectHeight++
            }
        }

        val widthAllChildrenAreIncorrect = childrenWithIncorrectWidth == items.size
        val widthHasMatchParentChild = childrenWithIncorrectWidth > 0
        val heightAllChildrenAreIncorrect = childrenWithIncorrectHeight == items.size
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

    private fun ViewGroup.dispatchBinding(
        divView: Div2View,
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        path: DivStatePath,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        val binder = divBinder.get()
        val items = newDiv.buildItems(resolver)
        var shift = 0
        items.forEachIndexed { index, item ->
            val childView = getChildAt(index + shift)
            val oldChildDiv = (childView as? DivHolderView<DivBase>)?.div

            val patchShift = applyPatchToChild(divView, newDiv, oldDiv, item.value(), index + shift, resolver, subscriber)
            if (patchShift > NO_PATCH_SHIFT) {
                shift += patchShift
            } else {
                binder.bind(childView, item, divView, path)
                childView.bindChildAlignment(newDiv, oldDiv, item.value(), oldChildDiv, resolver, subscriber)
            }
        }
    }

    private fun ViewGroup.applyPatchToChild(
        divView: Div2View,
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        childDiv: DivBase,
        childIndex: Int,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ): Int {
        childDiv.id?.let { id ->
            val patchViewsToAdd = divPatchManager.createViewsForId(divView, id) ?: return NO_PATCH_SHIFT
            val patchDivs = divPatchCache.getPatchDivListById(divView.dataTag, id) ?: return NO_PATCH_SHIFT
            removeViewAt(childIndex)
            patchViewsToAdd.forEachIndexed { patchIndex, patchView ->
                val patchDivValue = patchDivs[patchIndex].value()
                addView(patchView, childIndex + patchIndex)
                patchView.bindChildAlignment(newDiv, oldDiv, patchDivValue, null, resolver, subscriber)
                if (patchDivValue.hasSightActions) {
                    divView.bindViewToDiv(patchView, patchDivs[patchIndex])
                }
            }
            return patchViewsToAdd.size - 1
        }
        return NO_PATCH_SHIFT
    }

    private fun ViewGroup.replaceWithReuse(oldItems: List<Div>, newItems: List<Div>, divView: Div2View) {
        val resolver = divView.expressionResolver

        val oldChildren = mutableMapOf<Div, View>()
        oldItems.zip(children.toList()) { childDiv, child ->
            oldChildren[childDiv] = child
        }

        removeAllViews()

        val createViewIndices = mutableListOf<Int>()

        newItems.forEachIndexed { index, newChild ->
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
            val newChildDiv = newItems[index]

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

    private fun <T> T.bindClipChildren(
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        resolver: ExpressionResolver
    ) where T : ViewGroup, T : DivHolderView<*> {
        if (newDiv.clipToBounds.equalsToConstant(oldDiv?.clipToBounds)) {
            return
        }

        applyClipChildren(newDiv.clipToBounds.evaluate(resolver))

        if (newDiv.clipToBounds.isConstant()) {
            return
        }

        addSubscription(newDiv.clipToBounds.observe(resolver) { clip -> applyClipChildren(clip) })
    }

    private fun <T> T.applyClipChildren(clip: Boolean) where T : ViewGroup, T : DivHolderView<*> {
        needClipping = clip
        val parent = parent
        if (!clip && parent is ViewGroup) {
            parent.clipChildren = false
        }
    }

    private fun DivLinearLayout.bindProperties(
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        resolver: ExpressionResolver
    ) {
        bindOrientation(newDiv, oldDiv, resolver) { orientation ->
            this.orientation = orientation.toOrientationMode()
        }
        bindContentAlignment(newDiv, oldDiv, resolver) { horizontalAlignment, verticalAlignment ->
            gravity = evaluateGravity(horizontalAlignment, verticalAlignment)
        }
        bindSeparator(newDiv, oldDiv, resolver)
    }

    private fun DivLinearLayout.bindSeparator(
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        resolver: ExpressionResolver
    ) {
        bindSeparatorShowMode(newDiv.separator, oldDiv?.separator, resolver) { separator, _ ->
            showDividers = separator.toSeparatorMode(resolver)
        }
        bindSeparatorStyle(newDiv.separator, oldDiv?.separator, resolver) { style, _ ->
            dividerDrawable = style?.toDrawable(resources.displayMetrics, resolver)
        }
        bindSeparatorMargins(newDiv.separator, oldDiv?.separator, resolver) { margins, _ ->
            val rect = margins.toRect(resources, resolver)
            setDividerMargins(rect.left, rect.top, rect.right, rect.bottom)
        }
    }

    private fun DivWrapLayout.bindProperties(
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        resolver: ExpressionResolver
    ) {
        bindOrientation(newDiv, oldDiv, resolver) { orientation ->
            wrapDirection = orientation.toWrapDirection()
        }
        bindContentAlignment(newDiv, oldDiv, resolver) { horizontalAlignment, verticalAlignment ->
            gravity = evaluateGravity(horizontalAlignment, verticalAlignment)
        }
        bindSeparator(newDiv, oldDiv, resolver)
        bindLineSeparator(newDiv, oldDiv, resolver)
    }

    private inline fun <T> T.bindOrientation(
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        resolver: ExpressionResolver,
        crossinline applyOrientation: (orientation: DivContainer.Orientation) -> Unit
    ) where T : ViewGroup, T : DivHolderView<DivContainer> {
        if (newDiv.orientation.equalsToConstant(oldDiv?.orientation)) {
            return
        }

        applyOrientation(newDiv.orientation.evaluate(resolver))

        if (newDiv.orientation.isConstant()) {
            return
        }

        addSubscription(
            newDiv.orientation.observe(resolver) { orientation -> applyOrientation(orientation) }
        )
    }

    private fun DivWrapLayout.applyOrientation(orientation: DivContainer.Orientation) {
        wrapDirection = if (orientation == DivContainer.Orientation.HORIZONTAL) {
            WrapDirection.ROW
        } else {
            WrapDirection.COLUMN
        }
    }

    private inline fun <T> T.bindContentAlignment(
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        resolver: ExpressionResolver,
        crossinline applyContentAlignment: (DivContentAlignmentHorizontal, DivContentAlignmentVertical) -> Unit
    ) where T : ViewGroup, T : DivHolderView<DivContainer> {
        if (newDiv.contentAlignmentHorizontal.equalsToConstant(oldDiv?.contentAlignmentHorizontal)
            && newDiv.contentAlignmentVertical.equalsToConstant(oldDiv?.contentAlignmentVertical)) {
            return
        }

        applyContentAlignment(
            newDiv.contentAlignmentHorizontal.evaluate(resolver),
            newDiv.contentAlignmentVertical.evaluate(resolver)
        )

        if (newDiv.contentAlignmentHorizontal.isConstant() && newDiv.contentAlignmentVertical.isConstant()) {
            return
        }

        val callback = { _: Any ->
            applyContentAlignment(
                newDiv.contentAlignmentHorizontal.evaluate(resolver),
                newDiv.contentAlignmentVertical.evaluate(resolver)
            )
        }
        addSubscription(newDiv.contentAlignmentHorizontal.observe(resolver, callback))
        addSubscription(newDiv.contentAlignmentVertical.observe(resolver, callback))
    }

    private fun DivWrapLayout.bindSeparator(
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        resolver: ExpressionResolver
    ) {
        bindSeparatorShowMode(newDiv.separator, oldDiv?.separator, resolver) { separator, _ ->
            showSeparators = separator.toSeparatorMode(resolver)
        }
        bindSeparatorStyle(newDiv.separator, oldDiv?.separator, resolver) { style, _ ->
            separatorDrawable = style?.toDrawable(resources.displayMetrics, resolver)
        }
        bindSeparatorMargins(newDiv.separator, oldDiv?.separator, resolver) { margins, _ ->
            val rect = margins.toRect(resources, resolver)
            setSeparatorMargins(rect.left, rect.top, rect.right, rect.bottom)
        }
    }

    private fun DivWrapLayout.bindLineSeparator(
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        resolver: ExpressionResolver
    ) {
        bindSeparatorShowMode(newDiv.lineSeparator, oldDiv?.lineSeparator, resolver) { separator, _ ->
            showLineSeparators = separator.toSeparatorMode(resolver)
        }
        bindSeparatorStyle(newDiv.lineSeparator, oldDiv?.lineSeparator, resolver) { style, _ ->
            lineSeparatorDrawable = style?.toDrawable(resources.displayMetrics, resolver)
        }
        bindSeparatorMargins(newDiv.lineSeparator, oldDiv?.lineSeparator, resolver) { margins, _ ->
            val rect = margins.toRect(resources, resolver)
            setLineSeparatorMargins(rect.left, rect.top, rect.right, rect.bottom)
        }
    }

    private inline fun <T> T.bindSeparatorShowMode(
        newSeparator: DivContainer.Separator?,
        oldSeparator: DivContainer.Separator?,
        resolver: ExpressionResolver,
        crossinline applySeparatorShowMode: (DivContainer.Separator?, ExpressionResolver) -> Unit
    ) where T : ViewGroup, T : DivHolderView<DivContainer> {
        if (newSeparator?.showAtStart.equalsToConstant(oldSeparator?.showAtStart)
            && newSeparator?.showBetween.equalsToConstant(oldSeparator?.showBetween)
            && newSeparator?.showAtEnd.equalsToConstant(oldSeparator?.showAtEnd)) {
            return
        }

        applySeparatorShowMode(newSeparator, resolver)

        if (newSeparator?.showAtStart.isConstantOrNull()
            && newSeparator?.showBetween.isConstantOrNull()
            && newSeparator?.showAtEnd.isConstantOrNull()) {
            return
        }

        val callback: (Any) -> Unit = { applySeparatorShowMode(newSeparator, resolver) }
        addSubscription(newSeparator?.showAtStart?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(newSeparator?.showBetween?.observe(resolver, callback) ?: Disposable.NULL)
        addSubscription(newSeparator?.showAtEnd?.observe(resolver, callback) ?: Disposable.NULL)
    }

    private inline fun <T> T.bindSeparatorStyle(
        newSeparator: DivContainer.Separator?,
        oldSeparator: DivContainer.Separator?,
        resolver: ExpressionResolver,
        crossinline applySeparatorStyle: (DivDrawable?, ExpressionResolver) -> Unit
    ) where T : ViewGroup, T : DivHolderView<DivContainer> {
        if (newSeparator?.style.equalsToConstant(oldSeparator?.style)) {
            return
        }

        applySeparatorStyle(newSeparator?.style, resolver)

        if (newSeparator?.style?.isConstant() != false) {
            return
        }

        val callback: (Any) -> Unit = { applySeparatorStyle(newSeparator.style, resolver) }
        observeDrawable(newSeparator.style, resolver, callback)
    }

    private inline fun <T> T.bindSeparatorMargins(
        newSeparator: DivContainer.Separator?,
        oldSeparator: DivContainer.Separator?,
        resolver: ExpressionResolver,
        crossinline applySeparatorMargins: (DivEdgeInsets?, ExpressionResolver) -> Unit
    ) where T : ViewGroup, T : DivHolderView<DivContainer> {
        if (newSeparator?.margins.equalsToConstant(oldSeparator?.margins)) {
            return
        }

        applySeparatorMargins(newSeparator?.margins, resolver)

        val margins = newSeparator?.margins
        if (margins?.isConstant() != false) {
            return
        }

        val callback = { _: Any -> applySeparatorMargins(margins, resolver) }
        addSubscription(margins.top.observe(resolver, callback))
        addSubscription(margins.bottom.observe(resolver, callback))
        if (margins.start != null || margins.end != null) {
            addSubscription(margins.start?.observe(resolver, callback) ?: Disposable.NULL)
            addSubscription(margins.end?.observe(resolver, callback) ?: Disposable.NULL)
        } else {
            addSubscription(margins.left.observe(resolver, callback))
            addSubscription(margins.right.observe(resolver, callback))
        }
    }

    @Deprecated(message = "use View.bindChildAlignment(DivContainer, DivContainer?, DivBase, DivBase?, ExpressionResolver, ExpressionSubscriber) instead")
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

    private fun View.bindChildAlignment(
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        newChildDiv: DivBase,
        oldChildDiv: DivBase?,
        resolver: ExpressionResolver,
        subscriber: ExpressionSubscriber
    ) {
        if (newDiv.contentAlignmentHorizontal.equalsToConstant(oldDiv?.contentAlignmentHorizontal)
            && newDiv.contentAlignmentVertical.equalsToConstant(oldDiv?.contentAlignmentVertical)
            && newChildDiv.alignmentHorizontal.equalsToConstant(oldChildDiv?.alignmentHorizontal)
            && newChildDiv.alignmentVertical.equalsToConstant(oldChildDiv?.alignmentVertical)) {
            return
        }

        applyChildAlignment(newDiv, newChildDiv, resolver)

        if (newDiv.contentAlignmentHorizontal.isConstant()
            && newDiv.contentAlignmentVertical.isConstant()
            && newChildDiv.alignmentHorizontal.isConstantOrNull()
            && newChildDiv.alignmentVertical.isConstantOrNull()) {
            return
        }

        val callback = { _: Any -> applyChildAlignment(newDiv, newChildDiv, resolver) }
        subscriber.addSubscription(newDiv.contentAlignmentHorizontal.observe(resolver, callback))
        subscriber.addSubscription(newDiv.contentAlignmentVertical.observe(resolver, callback))
        subscriber.addSubscription(newChildDiv.alignmentHorizontal?.observe(resolver, callback) ?: Disposable.NULL)
        subscriber.addSubscription(newChildDiv.alignmentVertical?.observe(resolver, callback) ?: Disposable.NULL)
    }

    private fun View.applyChildAlignment(
        div: DivContainer,
        childDiv: DivBase,
        resolver: ExpressionResolver,
    ) {
        val childAlignmentHorizontal = childDiv.alignmentHorizontal
        val alignmentHorizontal = when {
            childAlignmentHorizontal != null -> childAlignmentHorizontal.evaluate(resolver)
            div.isWrapContainer(resolver) -> null
            else -> div.contentAlignmentHorizontal.evaluate(resolver).toAlignmentHorizontal()
        }

        val childAlignmentVertical = childDiv.alignmentVertical
        val alignmentVertical = when {
            childAlignmentVertical != null -> childAlignmentVertical.evaluate(resolver)
            div.isWrapContainer(resolver) -> null
            else -> div.contentAlignmentVertical.evaluate(resolver).toAlignmentVertical()
        }

        applyAlignment(alignmentHorizontal, alignmentVertical)
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

    fun setDataWithoutBinding(view: ViewGroup, div: DivContainer, resolver: ExpressionResolver) {
        @Suppress("UNCHECKED_CAST")
        (view as DivHolderView<DivContainer>).div = div
        val binder = divBinder.get()
        div.buildItems(resolver).forEachIndexed { index, item ->
            binder.setDataWithoutBinding(view.getChildAt(index), item, resolver)
        }
    }

    private fun DivEdgeInsets?.toRect(resources: Resources, resolver: ExpressionResolver): Rect {
        if (this == null) {
            tempRect.set(0, 0, 0, 0)
            return tempRect
        }

        val metrics = resources.displayMetrics
        val sizeUnit = unit.evaluate(resolver)

        if (start != null || end != null) {
            val layoutDirection = resources.configuration.layoutDirection
            if (layoutDirection == View.LAYOUT_DIRECTION_LTR) {
                tempRect.left = start?.evaluate(resolver).unitToPx(metrics, sizeUnit)
                tempRect.right = end?.evaluate(resolver).unitToPx(metrics, sizeUnit)
            } else {
                tempRect.left = end?.evaluate(resolver).unitToPx(metrics, sizeUnit)
                tempRect.right = start?.evaluate(resolver).unitToPx(metrics, sizeUnit)
            }
        } else {
            tempRect.left = left.evaluate(resolver).unitToPx(metrics, sizeUnit)
            tempRect.right = right.evaluate(resolver).unitToPx(metrics, sizeUnit)
        }
        tempRect.top = top.evaluate(resolver).unitToPx(metrics, sizeUnit)
        tempRect.bottom = bottom.evaluate(resolver).unitToPx(metrics, sizeUnit)

        return tempRect
    }

    private fun DivContainer.Orientation.toOrientationMode(): Int {
        return when (this) {
            DivContainer.Orientation.HORIZONTAL -> LinearLayout.HORIZONTAL
            else -> LinearLayout.VERTICAL
        }
    }

    @WrapDirection
    private fun DivContainer.Orientation.toWrapDirection(): Int {
        return when (this) {
            DivContainer.Orientation.HORIZONTAL -> WrapDirection.ROW
            else -> WrapDirection.COLUMN
        }
    }

    private fun DivContainer.Separator?.toSeparatorMode(resolver: ExpressionResolver): Int {
        if (this == null) {
            return ShowSeparatorsMode.NONE
        }

        var separatorMode = ShowSeparatorsMode.NONE
        if (showAtStart.evaluate(resolver)) {
            separatorMode = separatorMode or ShowSeparatorsMode.SHOW_AT_START
        }
        if (showBetween.evaluate(resolver)) {
            separatorMode = separatorMode or ShowSeparatorsMode.SHOW_BETWEEN
        }
        if (showAtEnd.evaluate(resolver)) {
            separatorMode = separatorMode or ShowSeparatorsMode.SHOW_AT_END
        }
        return separatorMode
    }
}
