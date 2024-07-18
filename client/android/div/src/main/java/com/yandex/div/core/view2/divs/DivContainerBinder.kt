package com.yandex.div.core.view2.divs

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
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
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivBinder
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivViewCreator
import com.yandex.div.core.view2.animations.DivComparator
import com.yandex.div.core.view2.divs.widgets.DivCollectionHolder
import com.yandex.div.core.view2.divs.widgets.DivHolderView
import com.yandex.div.core.view2.divs.widgets.DivLinearLayout
import com.yandex.div.core.view2.divs.widgets.DivWrapLayout
import com.yandex.div.core.view2.divs.widgets.visitViewTree
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.errors.ErrorCollectors
import com.yandex.div.core.view2.reuse.util.tryRebindPlainContainerChildren
import com.yandex.div.core.widget.AspectView
import com.yandex.div.core.widget.ShowSeparatorsMode
import com.yandex.div.core.widget.wraplayout.WrapDirection
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.getItemResolver
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

    override fun bindView(context: BindingContext, view: ViewGroup, div: DivContainer, path: DivStatePath) {
        @Suppress("UNCHECKED_CAST")
        val divHolderView = view as DivHolderView<DivContainer>
        val oldDiv = divHolderView.div
        val divView = context.divView
        val oldResolver = divHolderView.bindingContext?.expressionResolver ?: divView.oldExpressionResolver

        baseBinder.bindView(context, view, div, oldDiv)
        view.applyDivActions(
            context,
            div.action,
            div.actions,
            div.longtapActions,
            div.doubletapActions,
            div.actionAnimation,
            div.accessibility,
        )

        val resolver = context.expressionResolver
        val errorCollector = errorCollectors.getOrCreate(divView.dataTag, divView.divData)

        view.bindAspectRatio(div.aspect, oldDiv?.aspect, resolver)

        when (view) {
            is DivLinearLayout -> view.bindProperties(div, oldDiv, resolver)
            is DivWrapLayout -> view.bindProperties(div, oldDiv, resolver)
        }

        view.bindClipChildren(div, oldDiv, resolver)

        for (childView in view.children) {
            divView.unbindViewFromDiv(childView)
        }

        view.bindItems(context, div, oldDiv, oldResolver, path, errorCollector)
    }

    private fun ViewGroup.bindItems(
        context: BindingContext,
        div: DivContainer,
        oldDiv: DivContainer?,
        oldResolver: ExpressionResolver,
        path: DivStatePath,
        errorCollector: ErrorCollector,
    ) {
        val divView = context.divView
        val resolver = context.expressionResolver
        val items = div.buildItems(resolver)

        val oldItems = (this as DivCollectionHolder).items?.let {
            when {
                div === oldDiv -> it
                divView.complexRebindInProgress -> null
                oldDiv != null &&
                    DivComparator.areValuesReplaceable(oldDiv, div, oldResolver, context.expressionResolver) &&
                    DivComparator.areChildrenReplaceable(it, items) -> it

                else -> {
                    replaceWithReuse(divView, it, items)
                    null
                }
            }
        }
        bindItemBuilder(context, div, path, errorCollector)
        applyItems(context, div, oldDiv, items, oldItems, path, errorCollector)
    }

    private fun ViewGroup.bindItemBuilder(
        context: BindingContext,
        div: DivContainer,
        path: DivStatePath,
        errorCollector: ErrorCollector,
    ) {
        val builder = div.itemBuilder ?: return

        val callback = { _: Any ->
            val newItems = div.buildItems(context.expressionResolver)
            val oldItems = (this as DivCollectionHolder).items ?: emptyList()
            replaceWithReuse(context.divView, oldItems, newItems)
            applyItems(context, div, div, newItems, oldItems, path, errorCollector)
        }
        builder.data.observe(context.expressionResolver, callback)

        val itemResolver = builder.getItemResolver(context.expressionResolver)
        builder.prototypes.forEach {
            it.selector.observe(itemResolver, callback)
        }
    }

    private fun ViewGroup.applyItems(
        context: BindingContext,
        div: DivContainer,
        oldDiv: DivContainer?,
        items: List<DivItemBuilderResult>,
        oldItems: List<DivItemBuilderResult>?,
        path: DivStatePath,
        errorCollector: ErrorCollector,
    ) {
        (this as DivCollectionHolder).items = items

        val divView = context.divView
        tryRebindPlainContainerChildren(divView, items, divViewCreator)

        validateChildren(div, items, context.expressionResolver, errorCollector)
        dispatchBinding(context, div, oldDiv, items, path)

        items.forEachIndexed { i, item ->
            if (item.div.value().hasSightActions) {
                divView.bindViewToDiv(getChildAt(i), item.div)
            }
        }
        trackVisibilityActions(divView, items, oldItems)
    }

    private fun ViewGroup.validateChildren(
        div: DivContainer,
        items: List<DivItemBuilderResult>,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector
    ) {
        var childrenWithIncorrectWidth = 0
        var childrenWithIncorrectHeight = 0

        items.forEach { item ->
            val childDivValue = item.div.value()

            if (this is DivWrapLayout) {
                div.checkCrossAxisSize(childDivValue, resolver, errorCollector)
            } else {
                if (div.hasIncorrectWidth(childDivValue)) childrenWithIncorrectWidth++
                if (div.hasIncorrectHeight(childDivValue, resolver)) childrenWithIncorrectHeight++
            }
        }

        val widthHasMatchParentChild = childrenWithIncorrectWidth > 0
        val widthAllChildrenAreIncorrect = widthHasMatchParentChild && childrenWithIncorrectWidth == items.size
        val heightHasMatchParentChild = childrenWithIncorrectHeight > 0
        val heightAllChildrenAreIncorrect = heightHasMatchParentChild && childrenWithIncorrectHeight == items.size

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
        bindingContext: BindingContext,
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        items: List<DivItemBuilderResult>,
        path: DivStatePath,
    ) {
        val binder = divBinder.get()
        var shift = 0
        val subscriber = expressionSubscriber
        items.forEachIndexed { index, item ->
            val childView = getChildAt(index + shift)
            val oldChildDiv = (childView as? DivHolderView<*>)?.div

            val patchShift = newDiv.itemBuilder?.let { NO_PATCH_SHIFT } ?: applyPatchToChild(
                bindingContext,
                newDiv,
                oldDiv,
                item.div.value(),
                index + shift,
                subscriber
            )

            if (patchShift > NO_PATCH_SHIFT) {
                shift += patchShift
            } else {
                val childBindingContext = bindingContext.getFor(item.expressionResolver)
                binder.bind(childBindingContext, childView, item.div, path)
                childView.bindChildAlignment(
                    newDiv,
                    oldDiv,
                    item.div.value(),
                    oldChildDiv,
                    bindingContext.expressionResolver,
                    item.expressionResolver,
                    subscriber,
                    bindingContext.divView
                )
            }
        }
    }

    private fun ViewGroup.applyPatchToChild(
        bindingContext: BindingContext,
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        childDiv: DivBase,
        childIndex: Int,
        subscriber: ExpressionSubscriber
    ): Int {
        val divView = bindingContext.divView
        childDiv.id?.let { id ->
            val patchViewsToAdd = divPatchManager.createViewsForId(bindingContext, id) ?: return NO_PATCH_SHIFT
            val patchDivs = divPatchCache.getPatchDivListById(divView.dataTag, id) ?: return NO_PATCH_SHIFT
            removeViewAt(childIndex)
            patchViewsToAdd.forEachIndexed { patchIndex, patchView ->
                val patchDivValue = patchDivs[patchIndex].value()
                addView(patchView, childIndex + patchIndex)
                patchView.bindChildAlignment(
                    newDiv = newDiv,
                    oldDiv = oldDiv,
                    newChildDiv = patchDivValue,
                    oldChildDiv = null,
                    resolver = bindingContext.expressionResolver,
                    childResolver = bindingContext.expressionResolver,
                    subscriber = subscriber,
                    divView = divView
                )
                if (patchDivValue.hasSightActions) {
                    divView.bindViewToDiv(patchView, patchDivs[patchIndex])
                }
            }
            return patchViewsToAdd.size - 1
        }
        return NO_PATCH_SHIFT
    }

    private fun ViewGroup.replaceWithReuse(
        divView: Div2View,
        oldItems: List<DivItemBuilderResult>,
        newItems: List<DivItemBuilderResult>,
    ) {
        val oldChildren = mutableMapOf<Div, View>()
        oldItems.zip(children.toList()) { childDiv, child ->
            oldChildren[childDiv.div] = child
        }

        removeAllViews()

        val createViewIndices = mutableListOf<Int>()

        newItems.forEachIndexed { index, newChild ->
            val oldViewIndex = oldChildren.keys.firstOrNull { oldChildDiv ->
                if (oldChildDiv.isBranch) {
                    newChild.div.type == oldChildDiv.type
                } else {
                    oldChildDiv.canBeReused(newChild.div, newChild.expressionResolver)
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
            val newChild = newItems[index]

            val oldViewIndex = oldChildren.keys.firstOrNull { oldChildDiv ->
                oldChildDiv.type == newChild.div.type
            }

            val childView = oldChildren
                .remove(oldViewIndex)
                ?: divViewCreator.get().create(newChild.div, newChild.expressionResolver)

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
        addSubscription(newSeparator?.showAtStart?.observe(resolver, callback))
        addSubscription(newSeparator?.showBetween?.observe(resolver, callback))
        addSubscription(newSeparator?.showAtEnd?.observe(resolver, callback))
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

        if (newSeparator?.style.isConstant()) {
            return
        }

        val callback: (Any) -> Unit = { applySeparatorStyle(newSeparator?.style, resolver) }
        observeDrawable(newSeparator?.style, resolver, callback)
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
            addSubscription(margins.start?.observe(resolver, callback))
            addSubscription(margins.end?.observe(resolver, callback))
        } else {
            addSubscription(margins.left.observe(resolver, callback))
            addSubscription(margins.right.observe(resolver, callback))
        }
    }

    private fun View.bindChildAlignment(
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        newChildDiv: DivBase,
        oldChildDiv: DivBase?,
        resolver: ExpressionResolver,
        childResolver: ExpressionResolver,
        subscriber: ExpressionSubscriber,
        divView: Div2View,
    ) {
        if (!divView.complexRebindInProgress
            && newDiv.contentAlignmentHorizontal.equalsToConstant(oldDiv?.contentAlignmentHorizontal)
            && newDiv.contentAlignmentVertical.equalsToConstant(oldDiv?.contentAlignmentVertical)
            && newChildDiv.alignmentHorizontal.equalsToConstant(oldChildDiv?.alignmentHorizontal)
            && newChildDiv.alignmentVertical.equalsToConstant(oldChildDiv?.alignmentVertical)) {
            return
        }

        applyChildAlignment(newDiv, newChildDiv, resolver, childResolver)

        if (newDiv.contentAlignmentHorizontal.isConstant()
            && newDiv.contentAlignmentVertical.isConstant()
            && newChildDiv.alignmentHorizontal.isConstantOrNull()
            && newChildDiv.alignmentVertical.isConstantOrNull()) {
            return
        }

        val callback = { _: Any -> applyChildAlignment(newDiv, newChildDiv, resolver, childResolver) }
        subscriber.addSubscription(newDiv.contentAlignmentHorizontal.observe(resolver, callback))
        subscriber.addSubscription(newDiv.contentAlignmentVertical.observe(resolver, callback))
        subscriber.addSubscription(newChildDiv.alignmentHorizontal?.observe(childResolver, callback))
        subscriber.addSubscription(newChildDiv.alignmentVertical?.observe(childResolver, callback))
    }

    private fun View.applyChildAlignment(
        div: DivContainer,
        childDiv: DivBase,
        resolver: ExpressionResolver,
        childResolver: ExpressionResolver,
    ) {
        val childAlignmentHorizontal = childDiv.alignmentHorizontal
        val alignmentHorizontal = when {
            childAlignmentHorizontal != null -> childAlignmentHorizontal.evaluate(childResolver)
            div.isWrapContainer(resolver) -> null
            else -> div.contentAlignmentHorizontal.evaluate(resolver).toAlignmentHorizontal()
        }

        val childAlignmentVertical = childDiv.alignmentVertical
        val alignmentVertical = when {
            childAlignmentVertical != null -> childAlignmentVertical.evaluate(childResolver)
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

    fun setDataWithoutBinding(bindingContext: BindingContext, view: ViewGroup, div: DivContainer) {
        @Suppress("UNCHECKED_CAST")
        (view as DivHolderView<DivContainer>).div = div
        val binder = divBinder.get()
        div.buildItems(bindingContext.expressionResolver).forEachIndexed { index, item ->
            val childView = view.getChildAt(index)
            val context = childView.bindingContext ?: bindingContext
            binder.setDataWithoutBinding(context, childView, item.div)
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
