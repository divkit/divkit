package com.yandex.div.core.view2.divs

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import com.yandex.div.core.dagger.DivScope
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
import com.yandex.div.core.view2.divs.widgets.DivFrameLayout
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
import com.yandex.div.internal.core.build
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
import com.yandex.div2.DivSize
import javax.inject.Inject
import javax.inject.Provider

private const val INCORRECT_CHILD_SIZE_MESSAGE = "Incorrect child size. " +
    "Container with %s contains child%s with match_parent size along the %s axis."
private const val WRAP_CONTENT_SIZE = "wrap_content size"
private const val WRAP_LAYOUT_MODE = "wrap layout mode"
private const val AXIS_MAIN = "main"
private const val AXIS_CROSS = "cross"

@DivScope
internal class DivContainerBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val divViewCreator: Provider<DivViewCreator>,
    private val divPatchManager: DivPatchManager,
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
        bindItemBuilder(builder, context.expressionResolver) {
            val newItems = builder.build(context.expressionResolver)
            val oldItems = (this as DivCollectionHolder).items ?: emptyList()
            replaceWithReuse(context.divView, oldItems, newItems)
            applyItems(context, div, div, newItems, oldItems, path, errorCollector)
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
        val divView = context.divView
        tryRebindPlainContainerChildren(divView, items, divViewCreator)

        validateChildren(div, items, context.expressionResolver, errorCollector)
        val dispatchedItems = dispatchBinding(context, div, oldDiv, items, path)

        dispatchedItems.forEachIndexed { i, item ->
            if (item.div.value().hasSightActions) {
                divView.bindViewToDiv(getChildAt(i), item.div)
            }
        }
        (this as DivCollectionHolder).items = dispatchedItems
        trackVisibilityActions(divView, dispatchedItems, oldItems)
    }

    private fun ViewGroup.validateChildren(
        div: DivContainer,
        items: List<DivItemBuilderResult>,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector
    ) {
        if (this is DivFrameLayout) return
        items.forEach { item ->
            val childDivValue = item.div.value()
            when (this) {
                is DivWrapLayout -> div.checkCrossAxisSize(childDivValue, resolver, errorCollector)
                is DivLinearLayout -> div.checkMainAxisSize(childDivValue, resolver, errorCollector)
            }
        }
    }

    private fun ViewGroup.dispatchBinding(
        bindingContext: BindingContext,
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        items: List<DivItemBuilderResult>,
        path: DivStatePath,
    ): List<DivItemBuilderResult> {
        val binder = divBinder.get()
        var shift = 0
        val subscriber = expressionSubscriber

        val patchedItems = items.flatMapIndexed { index, item ->
            newDiv.itemBuilder?.let { return@flatMapIndexed listOf(item) } ?: applyPatchToChild(
                bindingContext,
                item.div,
                index + shift
            ).map { div -> DivItemBuilderResult(div, item.expressionResolver) }.also { shift += it.size - 1 }
        }

        patchedItems.forEachIndexed { index, item ->
            val childView = getChildAt(index)
            val childDiv = item.div.value()
            val oldChildDiv = (childView as? DivHolderView<*>)?.div
            val childPath = childDiv.resolvePath(index, path)

            if (bindingContext.expressionResolver != item.expressionResolver) {
                resolveRuntime(
                    runtimeStore = bindingContext.runtimeStore,
                    div = childDiv,
                    path = childPath.fullPath,
                    resolver = item.expressionResolver
                )
            }

            val childContext = bindingContext.getFor(item.expressionResolver)
            binder.bind(childContext, childView, item.div, childPath)
            childView.bindChildAlignment(
                newDiv,
                oldDiv,
                childDiv,
                oldChildDiv,
                bindingContext.expressionResolver,
                item.expressionResolver,
                subscriber,
                bindingContext.divView
            )
            if (childDiv.hasSightActions) {
                bindingContext.divView.bindViewToDiv(childView, item.div)
            } else {
                bindingContext.divView.unbindViewFromDiv(childView)
            }
        }
        return patchedItems
    }

    private fun ViewGroup.applyPatchToChild(
        bindingContext: BindingContext,
        childDiv: Div,
        childIndex: Int
    ): List<Div> {
        val divView = bindingContext.divView
        val childId = childDiv.value().id
        if (childId != null) {
            val patch = divPatchManager.createViewsForId(bindingContext, childId) ?: return listOf(childDiv)
            removeViewAt(childIndex)
            var shift = 0
            patch.forEach { (patchDiv, patchView) ->
                addView(patchView, childIndex + shift++)
                val patchDivBase = patchDiv.value()
                if (patchDivBase.hasSightActions) {
                    divView.bindViewToDiv(patchView, patchDiv)
                }
            }
            return patch.keys.toList()
        }
        return listOf(childDiv)
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
    ) {
        if (isHorizontal(resolver)) {
            childDiv.height.checkCrossAxisSize(childDiv, errorCollector)
        } else {
            childDiv.width.checkCrossAxisSize(childDiv, errorCollector)
        }
    }

    private fun DivSize.checkCrossAxisSize(childDiv: DivBase, errorCollector: ErrorCollector) =
        checkSize(childDiv, errorCollector, WRAP_LAYOUT_MODE, AXIS_CROSS)

    private fun DivContainer.checkMainAxisSize(
        childDiv: DivBase,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector
    ) {
        when {
            isHorizontal(resolver) -> {
                if (width is DivSize.WrapContent) {
                    childDiv.width.checkMainAxisSize(childDiv, errorCollector)
                }
            }
            height !is DivSize.WrapContent -> Unit
            aspect?.let { it.ratio.evaluate(resolver).toFloat() == AspectView.DEFAULT_ASPECT_RATIO } ?: true ->
                childDiv.height.checkMainAxisSize(childDiv, errorCollector)
        }
    }

    private fun DivSize.checkMainAxisSize(childDiv: DivBase, errorCollector: ErrorCollector) =
        checkSize(childDiv, errorCollector, WRAP_CONTENT_SIZE, AXIS_MAIN)

    private fun DivSize.checkSize(childDiv: DivBase, errorCollector: ErrorCollector, mode: String, axis: String) {
        if (this is DivSize.MatchParent) {
            val withId = childDiv.id?.let { " with id='$it'" } ?: ""
            errorCollector.logWarning(Throwable(INCORRECT_CHILD_SIZE_MESSAGE.format(mode, withId, axis)))
        }
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
