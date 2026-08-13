package com.yandex.div.core.view2.divs

import android.content.res.Resources
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.children
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.util.equalsToConstant
import com.yandex.div.core.util.evaluateGravity
import com.yandex.div.core.util.expressionSubscriber
import com.yandex.div.core.util.hasSightActions
import com.yandex.div.core.util.isConstant
import com.yandex.div.core.util.isHorizontal
import com.yandex.div.core.util.isWrapContainer
import com.yandex.div.core.util.observeDrawable
import com.yandex.div.core.util.toAlignmentHorizontal
import com.yandex.div.core.util.toAlignmentVertical
import com.yandex.div.core.util.toDrawable
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
import com.yandex.div.core.view2.errors.ErrorCollector
import com.yandex.div.core.view2.reuse.util.tryRebindPlainContainerChildren
import com.yandex.div.core.widget.AspectView
import com.yandex.div.core.widget.ShowSeparatorsMode
import com.yandex.div.core.widget.wraplayout.WrapDirection
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.core.build
import com.yandex.div.internal.core.buildItems
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.equalsToConstant
import com.yandex.div.json.expressions.isConstant
import com.yandex.div.json.expressions.isConstantOrNull
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
private const val ITEM_SPACING_IGNORED_MESSAGE =
    "item_spacing will be ignored due to the 'separator' property."
private const val LINE_SPACING_IGNORED_MESSAGE =
    "line_spacing will be ignored due to the 'line_separator' property."
private const val WRAP_CONTENT_SIZE = "wrap_content size"
private const val WRAP_LAYOUT_MODE = "wrap layout mode"
private const val AXIS_MAIN = "main"
private const val AXIS_CROSS = "cross"

@DivScope
internal class DivContainerBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val divViewCreator: Provider<DivViewCreator>,
    private val divBinder: Provider<DivBinder>,
) : DivViewBinder<DivBlock.Container, ViewGroup>(baseBinder) {

    private val tempRect = Rect()

    override fun bindView(view: ViewGroup, divBlock: DivBlock.Container, divView: Div2View) {
        @Suppress("UNCHECKED_CAST")
        val oldDivBlock = (view as DivHolderView<DivBlock.Container>).divBlock

        if (divBlock.div === oldDivBlock?.div) {
            view.bindItems(divBlock, oldDivBlock, divView, false)
            return
        }

        baseBinder.bindView(view, divBlock, oldDivBlock, divView)
        view.bind(divBlock, oldDivBlock, divView)

        for (childView in view.children) {
            divView.unbindViewFromDiv(childView)
        }

        view.bindItems(divBlock, oldDivBlock, divView)
    }

    override fun ViewGroup.bind(
        divBlock: DivBlock.Container,
        oldDivBlock: DivBlock.Container?,
        divView: Div2View,
    ) {
        val div = divBlock.divValue
        val oldDiv = oldDivBlock?.divValue
        val resolver = divBlock.expressionResolver

        applyDivActions(
            div.action,
            div.actions,
            div.longtapActions,
            div.doubletapActions,
            div.hoverStartActions,
            div.hoverEndActions,
            div.pressStartActions,
            div.pressEndActions,
            div.actionAnimation,
            div.captureFocusOnAction,
            resolver,
            divView,
        )

        bindAspectRatio(div.aspect, oldDiv?.aspect, resolver)
        bindClipChildren(div.clipToBounds, oldDiv?.clipToBounds, resolver)

        if (this is DivFrameLayout) return

        val errorCollector = divView.errorCollector
        when (this) {
            is DivLinearLayout -> bindProperties(div, oldDiv, resolver, errorCollector)
            is DivWrapLayout -> bindProperties(div, oldDiv, resolver, errorCollector)
        }
    }

    private fun ViewGroup.bindItems(
        divBlock: DivBlock.Container,
        oldDivBlock: DivBlock.Container?,
        divView: Div2View,
        shouldBindItemBuilder: Boolean = true,
    ) {
        val items = divBlock.buildItems()

        var oldItems = (this as DivCollectionHolder).items
        when {
            oldItems == null -> {
                items.forEach {
                    val child = divViewCreator.get().create(it.div, it.expressionResolver)
                    addView(child)
                }
            }
            divBlock === oldDivBlock -> Unit
            divView.complexRebindInProgress -> oldItems = null
            oldDivBlock != null &&
                DivComparator.areValuesReplaceable(oldDivBlock, divBlock) &&
                DivComparator.areChildrenReplaceable(oldItems, items) -> Unit

            else -> {
                replaceWithReuse(divView, divViewCreator, oldItems, items)
                oldItems = null
            }
        }
        if (shouldBindItemBuilder) bindItemBuilder(divBlock, divView)
        applyItems(divBlock, oldDivBlock, items, oldItems, divView)
    }

    private fun ViewGroup.bindItemBuilder(divBlock: DivBlock.Container, divView: Div2View) {
        val builder = divBlock.divValue.itemBuilder ?: return
        expressionSubscriber.bindItemBuilder(builder, divBlock.expressionResolver) {
            val newItems = builder.build(divBlock.expressionResolver, divBlock.path)
            val oldItems = (this as DivCollectionHolder).items ?: emptyList()
            replaceWithReuse(divView, divViewCreator, oldItems, newItems)
            applyItems(divBlock, divBlock, newItems, oldItems, divView)
        }
    }

    private fun ViewGroup.applyItems(
        parent: DivBlock.Container,
        oldParent: DivBlock.Container?,
        items: List<DivBlock>,
        oldItems: List<DivBlock>?,
        divView: Div2View,
    ) {
        tryRebindPlainContainerChildren(divView, items, divViewCreator)
        validateChildren(parent, items, divView.errorCollector)
        dispatchItems(parent, oldParent, items, oldItems, divView)
    }

    private fun ViewGroup.dispatchItems(
        parent: DivBlock.Container,
        oldParent: DivBlock.Container?,
        items: List<DivBlock>,
        oldItems: List<DivBlock>?,
        divView: Div2View,
    ) {
        items.forEachIndexed { index, item ->
            getChildAt(index).bindChild(item, parent, oldParent, divView)
        }
        (this as DivCollectionHolder).items = items
        trackVisibilityActions(divView, items, oldItems)
    }

    private fun ViewGroup.validateChildren(
        parent: DivBlock.Container,
        items: List<DivBlock>,
        errorCollector: ErrorCollector
    ) {
        if (this is DivFrameLayout) return
        items.forEach { item ->
            when (this) {
                is DivWrapLayout -> parent.checkCrossAxisSize(item, errorCollector)
                is DivLinearLayout -> parent.checkMainAxisSize(item, errorCollector)
            }
        }
    }

    private fun View.bindChild(
        child: DivBlock,
        parent: DivBlock.Container,
        oldParent: DivBlock.Container?,
        divView: Div2View,
    ) {
        val div = child.div
        val oldChild = this.divBlock

        divBinder.get().bind(this, child, divView)
        bindChildAlignment(parent, oldParent, child, oldChild, expressionSubscriber, divView)

        if (div.value().hasSightActions) {
            divView.bindViewToDiv(this, div)
        } else {
            divView.unbindViewFromDiv(this)
        }
    }

    private fun DivLinearLayout.bindProperties(
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
    ) {
        bindOrientation(newDiv, oldDiv, resolver) { orientation ->
            this.orientation = orientation.toOrientationMode()
        }
        bindContentAlignment(newDiv, oldDiv, resolver) { horizontalAlignment, verticalAlignment ->
            gravity = evaluateGravity(horizontalAlignment, verticalAlignment)
        }
        bindSeparator(newDiv, oldDiv, resolver)
        bindItemSpacing(newDiv, oldDiv, resolver) { itemSpacing ->
            setItemSpacing(itemSpacing.dpToPx(resources.displayMetrics))
        }
        checkItemSpacingIgnored(newDiv, resolver, errorCollector)
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

    private inline fun <T> T.bindItemSpacing(
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        resolver: ExpressionResolver,
        crossinline applyItemSpacing: (Long) -> Unit
    ) where T : ViewGroup, T : DivHolderView<DivBlock.Container> {
        if (newDiv.itemSpacing.equalsToConstant(oldDiv?.itemSpacing)) {
            return
        }

        applyItemSpacing(newDiv.itemSpacing.evaluate(resolver))

        if (newDiv.itemSpacing.isConstant()) {
            return
        }

        addSubscription(newDiv.itemSpacing.observe(resolver) { itemSpacing ->
            applyItemSpacing(itemSpacing)
        })
    }

    private fun DivWrapLayout.bindLineSpacing(
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        resolver: ExpressionResolver,
    ) {
        if (newDiv.lineSpacing.equalsToConstant(oldDiv?.lineSpacing)) return

        val displayMetrics = resources.displayMetrics
        setLineSpacing(newDiv.lineSpacing.evaluate(resolver).dpToPx(displayMetrics))

        if (newDiv.lineSpacing.isConstant()) return

        addSubscription(
            newDiv.lineSpacing.observe(resolver) { lineSpacing ->
                setLineSpacing(lineSpacing.dpToPx(displayMetrics))
            }
        )
    }

    private fun DivWrapLayout.bindProperties(
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
    ) {
        bindOrientation(newDiv, oldDiv, resolver) { orientation ->
            wrapDirection = orientation.toWrapDirection()
        }
        bindContentAlignment(newDiv, oldDiv, resolver) { horizontalAlignment, verticalAlignment ->
            gravity = evaluateGravity(horizontalAlignment, verticalAlignment)
        }
        bindSeparator(newDiv, oldDiv, resolver)
        bindLineSeparator(newDiv, oldDiv, resolver)
        bindItemSpacing(newDiv, oldDiv, resolver) { itemSpacing ->
            setItemSpacing(itemSpacing.dpToPx(resources.displayMetrics))
        }
        bindLineSpacing(newDiv, oldDiv, resolver)

        checkItemSpacingIgnored(newDiv, resolver, errorCollector)
        checkLineSpacingIgnored(newDiv, resolver, errorCollector)
    }

    private inline fun <T> T.bindOrientation(
        newDiv: DivContainer,
        oldDiv: DivContainer?,
        resolver: ExpressionResolver,
        crossinline applyOrientation: (orientation: DivContainer.Orientation) -> Unit
    ) where T : ViewGroup, T : DivHolderView<DivBlock.Container> {
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
    ) where T : ViewGroup, T : DivHolderView<DivBlock.Container> {
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
    ) where T : ViewGroup, T : DivHolderView<DivBlock.Container> {
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
    ) where T : ViewGroup, T : DivHolderView<DivBlock.Container> {
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
    ) where T : ViewGroup, T : DivHolderView<DivBlock.Container> {
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
        newDivBlock: DivBlock.Container,
        oldDivBlock: DivBlock.Container?,
        newChild: DivBlock,
        oldChild: DivBlock?,
        subscriber: ExpressionSubscriber,
        divView: Div2View,
    ) {
        val contentAlignmentHorizontal = newDivBlock.divValue.contentAlignmentHorizontal
        val contentAlignmentVertical = newDivBlock.divValue.contentAlignmentVertical
        val childAlignmentHorizontal = newChild.div.value().alignmentHorizontal
        val childAlignmentVertical = newChild.div.value().alignmentVertical

        if (!divView.complexRebindInProgress && oldChild != null &&
            contentAlignmentHorizontal.equalsToConstant(oldDivBlock?.divValue?.contentAlignmentHorizontal) &&
            contentAlignmentVertical.equalsToConstant(oldDivBlock?.divValue?.contentAlignmentVertical) &&
            childAlignmentHorizontal.equalsToConstant(oldChild.div.value().alignmentHorizontal) &&
            childAlignmentVertical.equalsToConstant(oldChild.div.value().alignmentVertical)) {
            return
        }

        applyChildAlignment(newDivBlock, newChild)

        if (contentAlignmentHorizontal.isConstant() && contentAlignmentVertical.isConstant() &&
            childAlignmentHorizontal.isConstantOrNull() && childAlignmentVertical.isConstantOrNull()) {
            return
        }

        val callback = { _: Any -> applyChildAlignment(newDivBlock, newChild) }
        subscriber.addSubscription(contentAlignmentHorizontal.observe(newDivBlock.expressionResolver, callback))
        subscriber.addSubscription(contentAlignmentVertical.observe(newDivBlock.expressionResolver, callback))
        subscriber.addSubscription(childAlignmentHorizontal?.observe(newChild.expressionResolver, callback))
        subscriber.addSubscription(childAlignmentVertical?.observe(newChild.expressionResolver, callback))
    }

    private fun View.applyChildAlignment(parent: DivBlock.Container, child: DivBlock) {
        val parentDiv = parent.divValue
        val parentResolver = parent.expressionResolver
        val childAlignmentHorizontal = child.div.value().alignmentHorizontal
        val alignmentHorizontal = when {
            childAlignmentHorizontal != null -> childAlignmentHorizontal.evaluate(child.expressionResolver)
            parentDiv.isWrapContainer(parentResolver) -> null
            else -> parentDiv.contentAlignmentHorizontal.evaluate(parentResolver).toAlignmentHorizontal()
        }

        val childAlignmentVertical = child.div.value().alignmentVertical
        val alignmentVertical = when {
            childAlignmentVertical != null -> childAlignmentVertical.evaluate(child.expressionResolver)
            parentDiv.isWrapContainer(parentResolver) -> null
            else -> parentDiv.contentAlignmentVertical.evaluate(parentResolver).toAlignmentVertical()
        }

        applyAlignment(alignmentHorizontal, alignmentVertical)
    }

    private fun DivBlock.Container.checkCrossAxisSize(child: DivBlock, errorCollector: ErrorCollector) {
        val childDiv = child.div.value()
        if (divValue.isHorizontal(expressionResolver)) {
            childDiv.height.checkCrossAxisSize(childDiv.id, errorCollector)
        } else {
            childDiv.width.checkCrossAxisSize(childDiv.id, errorCollector)
        }
    }

    private fun DivSize.checkCrossAxisSize(id: String?, errorCollector: ErrorCollector) =
        checkSize(id, errorCollector, WRAP_LAYOUT_MODE, AXIS_CROSS)

    private fun DivBlock.Container.checkMainAxisSize(child: DivBlock, errorCollector: ErrorCollector) {
        val childDiv = child.div.value()
        when {
            divValue.isHorizontal(expressionResolver) -> {
                if (divValue.width is DivSize.WrapContent) {
                    childDiv.width.checkMainAxisSize(childDiv.id, errorCollector)
                }
            }
            divValue.height !is DivSize.WrapContent -> Unit
            withoutAspect -> childDiv.height.checkMainAxisSize(childDiv.id, errorCollector)
        }
    }

    private val DivBlock.Container.withoutAspect: Boolean get() {
        val aspect = divValue.aspect ?: return true
        return aspect.ratio.evaluate(expressionResolver).toFloat() == AspectView.DEFAULT_ASPECT_RATIO
    }

    private fun DivSize.checkMainAxisSize(id: String?, errorCollector: ErrorCollector) =
        checkSize(id, errorCollector, WRAP_CONTENT_SIZE, AXIS_MAIN)

    private fun DivSize.checkSize(id: String?, errorCollector: ErrorCollector, mode: String, axis: String) {
        if (this is DivSize.MatchParent) {
            val withId = id?.let { " with id='$it'" } ?: ""
            errorCollector.logWarning(Throwable(INCORRECT_CHILD_SIZE_MESSAGE.format(mode, withId, axis)))
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

    private fun showSeparatorBetween(@ShowSeparatorsMode mode: Int): Boolean =
        (mode and ShowSeparatorsMode.SHOW_BETWEEN) != 0

    private fun checkItemSpacingIgnored(
        div: DivContainer,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
    ) {
        val itemSpacingValue = div.itemSpacing.evaluate(resolver)
        if (showSeparatorBetween(div.separator.toSeparatorMode(resolver)) && itemSpacingValue != 0L) {
            errorCollector.logWarning(Throwable(ITEM_SPACING_IGNORED_MESSAGE))
        }
    }

    private fun checkLineSpacingIgnored(
        div: DivContainer,
        resolver: ExpressionResolver,
        errorCollector: ErrorCollector,
    ) {
        val lineSpacingValue = div.lineSpacing.evaluate(resolver)
        if (showSeparatorBetween(div.lineSeparator.toSeparatorMode(resolver)) && lineSpacingValue != 0L) {
            errorCollector.logWarning(Throwable(LINE_SPACING_IGNORED_MESSAGE))
        }
    }
}
