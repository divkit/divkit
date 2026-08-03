package com.yandex.div.core.view2.divs

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.size
import com.yandex.div.R
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.DivActionPerformer
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment.IGNORE_ACTION_MENU_ITEMS_ENABLED
import com.yandex.div.core.experiments.Experiment.LONGTAP_ACTIONS_PASS_TO_CHILD_ENABLED
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivGestureListener
import com.yandex.div.core.view2.reuse.InputFocusTracker
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.util.allIsNullOrEmpty
import com.yandex.div.internal.widget.menu.OverflowMenuWrapper
import com.yandex.div.json.expressions.Expression
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import com.yandex.div2.DivAnimation
import java.util.UUID
import javax.inject.Inject

@DivScope
internal class DivActionBinder @Inject constructor(
    private val actionPerformer: DivActionPerformer,
    private val logger: Div2Logger,
    private val divActionBeaconSender: DivActionBeaconSender,
    @ExperimentFlag(LONGTAP_ACTIONS_PASS_TO_CHILD_ENABLED) private val longtapActionsPassToChild: Boolean,
    @ExperimentFlag(IGNORE_ACTION_MENU_ITEMS_ENABLED) private val shouldIgnoreActionMenuItems: Boolean,
) {

    private val passToParentLongClickListener: (View) -> Boolean = View::performLongClickOnAncestors

    fun bindDivActions(
        target: View,
        actions: List<DivAction>?,
        longTapActions: List<DivAction>?,
        doubleTapActions: List<DivAction>?,
        hoverStartActions: List<DivAction>?,
        hoverEndActions: List<DivAction>?,
        pressStartActions: List<DivAction>?,
        pressEndActions: List<DivAction>?,
        actionAnimation: DivAnimation,
        captureFocusOnAction: Expression<Boolean>,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) {
        val onApply = {
            applyDivActions(
                target = target,
                actions = actions.orEmpty(),
                doubleTapActions = doubleTapActions.orEmpty(),
                longTapActions = longTapActions.orEmpty(),
                hoverStartActions = hoverStartActions.orEmpty(),
                hoverEndActions = hoverEndActions.orEmpty(),
                pressStartActions = pressStartActions.orEmpty(),
                pressEndActions = pressEndActions.orEmpty(),
                actionAnimation = actionAnimation,
                captureFocusOnAction = captureFocusOnAction,
                resolver = resolver,
                divView = divView,
            )
        }
        with(target) {
            observe(actions, resolver) { onApply() }
            observe(longTapActions, resolver) { onApply() }
            observe(doubleTapActions, resolver) { onApply() }
            observe(captureFocusOnAction, resolver) { onApply() }
        }
        onApply()
    }

    private fun applyDivActions(
        target: View,
        actions: List<DivAction>,
        longTapActions: List<DivAction>,
        doubleTapActions: List<DivAction>,
        hoverStartActions: List<DivAction>,
        hoverEndActions: List<DivAction>,
        pressStartActions: List<DivAction>,
        pressEndActions: List<DivAction>,
        actionAnimation: DivAnimation,
        captureFocusOnAction: Expression<Boolean>,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) {
        val divGestureListener = DivGestureListener(
            awaitLongClick = longTapActions.isNotEmpty() || target.parentIsLongClickable()
        )
        bindLongTapActions(target, longTapActions, actions.isEmpty(), captureFocusOnAction, resolver, divView)
        bindDoubleTapActions(target, divGestureListener, doubleTapActions, captureFocusOnAction, resolver, divView)
        // Order is urgent: tap actions depend on double tap actions
        bindTapActions(
            target,
            divGestureListener,
            actions,
            resolver,
            divView,
            shouldIgnoreActionMenuItems,
            captureFocusOnAction,
            hasNonSingleTapActions = longTapActions.isNotEmpty() || doubleTapActions.isNotEmpty(),
        )

        val animatedTouchListener = target.createAnimatedTouchListener(
            actionAnimation.takeUnless { allIsNullOrEmpty(actions, longTapActions, doubleTapActions) },
            divGestureListener,
            resolver,
            divView,
        )
        val pressTouchListener = createPressTouchListener(target, pressStartActions, pressEndActions, resolver, divView)

        bindHoverActions(target, hoverStartActions, hoverEndActions, resolver, divView)

        target.attachTouchListeners(
            animatedTouchListener,
            pressTouchListener,
            FocusClickSynthesizer.create(target),
        )
    }

    private fun bindTapActions(
        target: View,
        divGestureListener: DivGestureListener,
        actions: List<DivAction>,
        resolver: ExpressionResolver,
        divView: Div2View,
        shouldIgnoreActionMenuItems: Boolean,
        captureFocusOnAction: Expression<Boolean>,
        hasNonSingleTapActions: Boolean,
    ) {
        if (actions.isEmpty()) {
            if (hasNonSingleTapActions) {
                target.setTapListener(divGestureListener) {
                    it.clearFocusIfNeeded(captureFocusOnAction, divView.inputFocusTracker, resolver)
                }
            } else {
                divGestureListener.onSingleTapListener = null
                target.setOnClickListener(null)
                target.isClickable = false
            }
            return
        }

        val menuAction = actions.firstOrNull { action ->
            !action.menuItems.isNullOrEmpty() && !shouldIgnoreActionMenuItems
        } ?: run {
            target.setTapListener(divGestureListener) {
                it.captureFocusIfNeeded(captureFocusOnAction, divView.inputFocusTracker, resolver)
                actionPerformer.performBulkActions(target, actions, resolver, divView)
            }
            return
        }

        prepareMenu(target, menuAction, resolver, divView) { overflowMenuWrapper ->
            target.setTapListener(divGestureListener) {
                logger.logClick(divView, resolver, target, menuAction)
                // Move focus as early as possible cause actions processing may move it elsewhere.
                it.captureFocusIfNeeded(captureFocusOnAction, divView.inputFocusTracker, resolver)
                divActionBeaconSender.sendTapActionBeacon(menuAction, resolver)
                overflowMenuWrapper.onMenuClickListener.onClick(target)
            }
        }
    }

    private fun View.setTapListener(
        gestureListener: DivGestureListener,
        listener: View.OnClickListener
    ) {
        // Single tap-up triggered with significant delay
        // so we'll use it only when double taps actually specified.
        if (gestureListener.onDoubleTapListener != null) {
            setOnClickListener(null)  // clear stale click listener to prevent double firing during view reuse
            gestureListener.onSingleTapListener = { listener.onClick(this) }
        } else {
            gestureListener.onSingleTapListener = null  // clear stale gesture listener to prevent double firing during view reuse
            setOnClickListener(listener)
        }
    }

    private fun bindLongTapActions(
        target: View,
        actions: List<DivAction>,
        noClickAction: Boolean,
        captureFocusOnAction: Expression<Boolean>,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) {
        if (actions.isEmpty()) {
            clearLongClickListener(target, longtapActionsPassToChild, noClickAction)
            return
        }

        if (longtapActionsPassToChild) {
            target.setPenetratingLongClickable()
        }

        val menuAction = actions.firstOrNull { action ->
            !action.menuItems.isNullOrEmpty() && !shouldIgnoreActionMenuItems
        } ?: run {
            target.setOnLongClickListener {
                it.captureFocusIfNeeded(captureFocusOnAction, divView.inputFocusTracker, resolver)
                actionPerformer.performBulkActions(target, actions, resolver, divView, DivActionReason.LONG_CLICK)
                return@setOnLongClickListener true
            }
            return
        }

        prepareMenu(target, menuAction, resolver, divView) { overflowMenuWrapper ->
            target.setOnLongClickListener {
                val uuid = UUID.randomUUID().toString()
                // Move focus as early as possible cause actions processing may move it elsewhere.
                it.captureFocusIfNeeded(captureFocusOnAction, divView.inputFocusTracker, resolver)
                divActionBeaconSender.sendTapActionBeacon(menuAction, resolver)
                overflowMenuWrapper.onMenuClickListener.onClick(target)
                actions.forEach { action ->
                    logger.logLongClick(divView, resolver, target, action, uuid)
                }
                return@setOnLongClickListener true
            }
        }
    }

    private fun clearLongClickListener(
        target: View,
        passLongTapsToChildren: Boolean,
        noClickAction: Boolean
    ) {
        if (!passLongTapsToChildren || noClickAction) {
            target.setOnLongClickListener(null)
            target.isLongClickable = false
            return
        }
        val isLongClickable = target.parentIsLongClickable()
        if (isLongClickable) {
            target.setOnLongClickListener(passToParentLongClickListener)
            target.setPenetratingLongClickable()
        } else {
            target.setOnLongClickListener(null)
            target.isLongClickable = false
            target.setPenetratingLongClickable(null)
        }
    }

    private fun bindDoubleTapActions(
        target: View,
        divGestureListener: DivGestureListener,
        actions: List<DivAction>,
        captureFocusOnAction: Expression<Boolean>,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) {
        if (actions.isEmpty()) {
            divGestureListener.onDoubleTapListener = null
            return
        }

        val menuAction = actions.firstOrNull { action ->
            !action.menuItems.isNullOrEmpty() && !shouldIgnoreActionMenuItems
        } ?: run {
            divGestureListener.onDoubleTapListener = {
                target.captureFocusIfNeeded(captureFocusOnAction, divView.inputFocusTracker, resolver)
                actionPerformer.performBulkActions(target, actions, resolver, divView, DivActionReason.DOUBLE_CLICK)
            }
            return
        }

        prepareMenu(target, menuAction, resolver, divView) { overflowMenuWrapper ->
            divGestureListener.onDoubleTapListener = {
                // Move focus as early as possible cause actions processing may move it elsewhere.
                target.captureFocusIfNeeded(captureFocusOnAction, divView.inputFocusTracker, resolver)
                logger.logDoubleClick(divView, resolver, target, menuAction)
                divActionBeaconSender.sendTapActionBeacon(menuAction, resolver)
                overflowMenuWrapper.onMenuClickListener.onClick(target)
            }
        }
    }

    private fun bindHoverActions(
        target: View,
        startActions: List<DivAction>,
        endActions: List<DivAction>,
        resolver: ExpressionResolver,
        divView: Div2View,
    ) {
        if (startActions.isEmpty() && endActions.isEmpty()) {
            target.setOnHoverListener(null)
            return
        }

        target.setOnHoverListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_HOVER_ENTER ->
                    actionPerformer.performBulkActions(target, startActions, resolver, divView, DivActionReason.HOVER)

                MotionEvent.ACTION_HOVER_EXIT ->
                    actionPerformer.performBulkActions(target, endActions, resolver, divView, DivActionReason.UNHOVER)
            }
            false
        }
    }

    private fun createPressTouchListener(
        target: View,
        pressStartActions: List<DivAction>,
        pressEndActions: List<DivAction>,
        resolver: ExpressionResolver,
        divView: Div2View,
    ): ((View, MotionEvent) -> Boolean)? {
        if (pressStartActions.isEmpty() && pressEndActions.isEmpty()) return null

        return { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    actionPerformer
                        .performBulkActions(target, pressStartActions, resolver, divView, DivActionReason.PRESS)
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    actionPerformer
                        .performBulkActions(target, pressEndActions, resolver, divView, DivActionReason.RELEASE)
                    true
                }
                else -> false
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun View.attachTouchListeners(vararg listeners: ((View, MotionEvent) -> Boolean)?) {
        val nnListeners = listeners.filterNotNull()

        if (nnListeners.isNotEmpty()) {
            setOnTouchListener { view, motionEvent ->
                nnListeners.fold(false) { acc, listener ->
                    listener(view, motionEvent) || acc
                }
            }
        } else {
            setOnTouchListener(null)
        }
    }

    internal fun handleTapClick(
        target: View,
        actions: List<DivAction>,
        resolver: ExpressionResolver,
        divView: Div2View
    ) {
        val menuAction = actions.firstOrNull { action ->
            action.isEnabled.evaluate(resolver) && !action.menuItems.isNullOrEmpty()
        } ?: run {
            actionPerformer.performBulkActions(target, actions, resolver, divView)
            return
        }

        prepareMenu(target, menuAction, resolver, divView) { overflowMenuWrapper ->
            logger.logClick(divView, resolver, target, menuAction)
            divActionBeaconSender.sendTapActionBeacon(menuAction, resolver)
            overflowMenuWrapper.onMenuClickListener.onClick(target)
        }
    }

    private inline fun prepareMenu(
        target: View,
        action: DivAction,
        resolver: ExpressionResolver,
        divView: Div2View,
        onPrepared: (OverflowMenuWrapper) -> Unit
    ) {
        val menuItems = action.menuItems ?: run {
            KAssert.fail { "Unable to bind empty menu action: ${action.logId}" }
            return
        }

        val overflowMenuWrapper = OverflowMenuWrapper(target.context, target, divView)
            .listener(MenuWrapperListener(menuItems, resolver, divView))
            .overflowGravity(Gravity.RIGHT or Gravity.TOP)

        divView.clearSubscriptions()
        divView.subscribe { overflowMenuWrapper.dismiss() }
        onPrepared(overflowMenuWrapper)
    }

    private inner class MenuWrapperListener(
        private val items: List<DivAction.MenuItem>,
        private val expressionResolver: ExpressionResolver,
        private val divView: Div2View,
    ) : OverflowMenuWrapper.Listener.Simple() {

        override fun onMenuCreated(popupMenu: PopupMenu) {
            val menu = popupMenu.menu
            for (itemData in items) {
                val itemPosition = menu.size
                val menuItem = menu.add(itemData.text.evaluate(expressionResolver))
                menuItem.setOnMenuItemClickListener {
                    var actionsHandled = false
                    divView.bulkActions {
                        val actions = itemData.actions.takeUnless { it.isNullOrEmpty() }
                            ?: itemData.action?.let(::listOf)
                        if (actions.isNullOrEmpty()) {
                            KAssert.fail { "Menu item does not have any action" }
                            return@bulkActions
                        }
                        actionPerformer.performMenuActions(divView, expressionResolver, actions, itemPosition, itemData)
                        actionsHandled = true
                    }
                    actionsHandled
                }
            }
        }
    }
}

private fun View.observe(
    actions: List<DivAction>?,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    if (this !is ExpressionSubscriber) return
    actions?.forEach { addSubscription(it.isEnabled.observe(resolver, callback)) }
}

private fun View.observe(
    captureFocusOnAction: Expression<Boolean>,
    resolver: ExpressionResolver,
    callback: (Any) -> Unit
) {
    if (this !is ExpressionSubscriber) return
    addSubscription(captureFocusOnAction.observe(resolver, callback))
}

private fun View.parentIsLongClickable(): Boolean {
    val parent = parent as? ViewGroup ?: return false
    return parent.isPenetratingLongClickable() || parent.parentIsLongClickable()
}

private fun View.isPenetratingLongClickable(): Boolean {
    return (getTag(R.id.div_penetrating_longtap_tag) as? Boolean) ?: false
}

// Catch longtap_actions in container child
private fun View.setPenetratingLongClickable(longClickable: Boolean? = true) {
    setTag(R.id.div_penetrating_longtap_tag, longClickable)
}

private fun View.clearFocusIfNeeded(
    captureFocusOnAction: Expression<Boolean>,
    inputFocusTracker: InputFocusTracker,
    expressionResolver: ExpressionResolver,
) {
    if (captureFocusOnAction.evaluate(expressionResolver)) {
        clearFocusOnClick(inputFocusTracker)
    }
}

private fun View.captureFocusIfNeeded(
    captureFocusOnAction: Expression<Boolean>,
    inputFocusTracker: InputFocusTracker,
    expressionResolver: ExpressionResolver,
) {
    if (captureFocusOnAction.evaluate(expressionResolver)) {
        clearFocusOnClick(inputFocusTracker)
        requestFocus()
    }
}
