package com.yandex.div.core.view2.divs

import android.annotation.SuppressLint
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.StringDef
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.PopupMenu
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.yandex.div.R
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.DivViewFacade
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment.ACCESSIBILITY_ENABLED
import com.yandex.div.core.experiments.Experiment.IGNORE_ACTION_MENU_ITEMS_ENABLED
import com.yandex.div.core.experiments.Experiment.LONGTAP_ACTIONS_PASS_TO_CHILD_ENABLED
import com.yandex.div.core.view2.AccessibilityDelegateWrapper
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivGestureListener
import com.yandex.div.core.view2.divs.DivActionBinder.LogType.Companion.LOG_BLUR
import com.yandex.div.core.view2.divs.DivActionBinder.LogType.Companion.LOG_CLICK
import com.yandex.div.core.view2.divs.DivActionBinder.LogType.Companion.LOG_DOUBLE_CLICK
import com.yandex.div.core.view2.divs.DivActionBinder.LogType.Companion.LOG_ENTER
import com.yandex.div.core.view2.divs.DivActionBinder.LogType.Companion.LOG_FOCUS
import com.yandex.div.core.view2.divs.DivActionBinder.LogType.Companion.LOG_HOVER
import com.yandex.div.core.view2.divs.DivActionBinder.LogType.Companion.LOG_LONG_CLICK
import com.yandex.div.core.view2.divs.DivActionBinder.LogType.Companion.LOG_PRESS
import com.yandex.div.core.view2.divs.DivActionBinder.LogType.Companion.LOG_RELEASE
import com.yandex.div.core.view2.divs.DivActionBinder.LogType.Companion.LOG_UNHOVER
import com.yandex.div.internal.Assert
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.core.ExpressionSubscriber
import com.yandex.div.internal.util.allIsNullOrEmpty
import com.yandex.div.internal.widget.menu.OverflowMenuWrapper
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAction
import com.yandex.div2.DivAnimation
import java.util.UUID
import javax.inject.Inject

@DivScope
@Mockable
internal class DivActionBinder @Inject constructor(
    private val actionHandler: DivActionHandler,
    private val logger: Div2Logger,
    private val divActionBeaconSender: DivActionBeaconSender,
    @ExperimentFlag(LONGTAP_ACTIONS_PASS_TO_CHILD_ENABLED) private val longtapActionsPassToChild: Boolean,
    @ExperimentFlag(IGNORE_ACTION_MENU_ITEMS_ENABLED) private val shouldIgnoreActionMenuItems: Boolean,
    @ExperimentFlag(ACCESSIBILITY_ENABLED) private val accessibilityEnabled: Boolean
) {
    private val passToParentLongClickListener: (View) -> Boolean = { view ->
        var isLongClickHandled = false
        var currentView: View = view
        do {
            currentView = currentView.parent as? ViewGroup ?: break
            if (currentView.parent == null) break
            isLongClickHandled = currentView.performLongClick()
        } while (!isLongClickHandled)

        isLongClickHandled
    }

    fun bindDivActions(
        context: BindingContext,
        target: View,
        actions: List<DivAction>?,
        longTapActions: List<DivAction>?,
        doubleTapActions: List<DivAction>?,
        hoverStartActions: List<DivAction>?,
        hoverEndActions: List<DivAction>?,
        pressStartActions: List<DivAction>?,
        pressEndActions: List<DivAction>?,
        actionAnimation: DivAnimation,
        accessibility: DivAccessibility?,
    ) {
        val resolver = context.expressionResolver
        val onApply = {
            applyDivActions(
                context = context,
                target = target,
                actions = actions.onlyEnabled(resolver),
                doubleTapActions = doubleTapActions.onlyEnabled(resolver),
                longTapActions = longTapActions.onlyEnabled(resolver),
                hoverStartActions = hoverStartActions.onlyEnabled(resolver),
                hoverEndActions = hoverEndActions.onlyEnabled(resolver),
                pressStartActions = pressStartActions.onlyEnabled(resolver),
                pressEndActions = pressEndActions.onlyEnabled(resolver),
                actionAnimation = actionAnimation,
                accessibility = accessibility,
            )
        }
        with(target) {
            observe(actions, resolver) { onApply() }
            observe(longTapActions, resolver) { onApply() }
            observe(doubleTapActions, resolver) { onApply() }
        }
        onApply()
    }

    private fun applyDivActions(
        context: BindingContext,
        target: View,
        actions: List<DivAction>,
        longTapActions: List<DivAction>,
        doubleTapActions: List<DivAction>,
        hoverStartActions: List<DivAction>,
        hoverEndActions: List<DivAction>,
        pressStartActions: List<DivAction>,
        pressEndActions: List<DivAction>,
        actionAnimation: DivAnimation,
        accessibility: DivAccessibility?,
    ) {
        val clickableState = target.isClickable
        val longClickableState = target.isLongClickable

        val divGestureListener = DivGestureListener(
            awaitLongClick = longTapActions.isNotEmpty() || target.parentIsLongClickable()
        )
        bindLongTapActions(context, target, longTapActions, actions.isEmpty())
        bindDoubleTapActions(context, target, divGestureListener, doubleTapActions)
        // Order is urgent: tap actions depend on double tap actions
        bindTapActions(context, target, divGestureListener, actions, shouldIgnoreActionMenuItems)

        val animatedTouchListener = target.createAnimatedTouchListener(
            context,
            actionAnimation.takeUnless { allIsNullOrEmpty(actions, longTapActions, doubleTapActions) },
            divGestureListener
        )
        val pressTouchListener = createPressTouchListener(context, target, pressStartActions, pressEndActions)

        bindHoverActions(context, target, hoverStartActions, hoverEndActions)

        target.attachTouchListeners(animatedTouchListener, pressTouchListener)

        if (accessibilityEnabled) {
            if (DivAccessibility.Mode.MERGE == context.divView.getPropagatedAccessibilityMode(target) &&
                context.divView.isDescendantAccessibilityMode(target)) {
                target.isClickable = clickableState
                target.isLongClickable = longClickableState
            }

            bindAccessibilityDelegate(target, actions, longTapActions, accessibility)
        }
    }

    private fun bindAccessibilityDelegate(
        target: View,
        actions: List<DivAction>,
        longTapActions: List<DivAction>,
        accessibility: DivAccessibility?,
    ) {
        val originalDelegate = ViewCompat.getAccessibilityDelegate(target)

        val action = { _: View?, info: AccessibilityNodeInfoCompat? ->
            if (actions.isNotEmpty()) {
                info?.addAction(AccessibilityNodeInfoCompat
                    .AccessibilityActionCompat.ACTION_CLICK)
            }
            if (longTapActions.isNotEmpty()) {
                info?.addAction(AccessibilityNodeInfoCompat
                    .AccessibilityActionCompat.ACTION_LONG_CLICK)
            }
            if (target is ImageView && (accessibility?.type == DivAccessibility.Type.AUTO || accessibility == null)) {
                if (longTapActions.isNotEmpty() || actions.isNotEmpty() || accessibility?.description != null) {
                    info?.className = "android.widget.ImageView"
                } else {
                    info?.className = ""
                }
            }
        }

        val accessibilityWrapper = if (originalDelegate is AccessibilityDelegateWrapper) {
            originalDelegate.actionsAccessibilityNodeInfo = action
            originalDelegate
        } else {
            AccessibilityDelegateWrapper(
                originalDelegate,
                actionsAccessibilityNodeInfo = action)
        }

        ViewCompat.setAccessibilityDelegate(target, accessibilityWrapper)
    }

    private fun bindTapActions(
        context: BindingContext,
        target: View,
        divGestureListener: DivGestureListener,
        actions: List<DivAction>,
        shouldIgnoreActionMenuItems: Boolean
    ) {
        if (actions.isEmpty()) {
            divGestureListener.onSingleTapListener = null
            target.setOnClickListener(null)
            target.isClickable = false
            return
        }

        val menuAction = actions.firstOrNull { action ->
            !action.menuItems.isNullOrEmpty() && !shouldIgnoreActionMenuItems
        }

        fun setTapListener(listener: View.OnClickListener) {
            // Single tap-up triggered with significant delay
            // so we'll use it only when double taps actually specified.
            if (divGestureListener.onDoubleTapListener != null) {
                divGestureListener.onSingleTapListener = { listener.onClick(target) }
            } else {
                target.setOnClickListener(listener)
            }
        }

        if (menuAction != null) {
            prepareMenu(target, context, menuAction) { overflowMenuWrapper ->
                setTapListener {
                    if (context.divView.inputFocusTracker.isOutsideVisibleArea()) {
                        it.clearFocusOnClick(context.divView.inputFocusTracker)
                    }
                    it.requestFocus()
                    logger.logClick(context.divView, context.expressionResolver, target, menuAction)
                    divActionBeaconSender.sendTapActionBeacon(menuAction, context.expressionResolver)
                    overflowMenuWrapper.onMenuClickListener.onClick(target)
                }
            }
        } else {
            setTapListener {
                if (context.divView.inputFocusTracker.isOutsideVisibleArea()) {
                    it.clearFocusOnClick(context.divView.inputFocusTracker)
                }
                it.requestFocus()
                handleBulkActions(context, target, actions)
            }
        }

    }

    private fun bindLongTapActions(
        context: BindingContext,
        target: View,
        actions: List<DivAction>,
        noClickAction: Boolean
    ) {
        if (actions.isEmpty()) {
            clearLongClickListener(target, longtapActionsPassToChild, noClickAction)
            return
        }

        val menuAction = actions.firstOrNull { action ->
            !action.menuItems.isNullOrEmpty() && !shouldIgnoreActionMenuItems
        }
        if (menuAction != null) {
            prepareMenu(target, context, menuAction) { overflowMenuWrapper ->
                target.setOnLongClickListener {
                    val uuid = UUID.randomUUID().toString()

                    divActionBeaconSender.sendTapActionBeacon(menuAction, context.expressionResolver)
                    overflowMenuWrapper.onMenuClickListener.onClick(target)
                    actions.forEach { action ->
                        logger.logLongClick(context.divView, context.expressionResolver, target, action, uuid)
                    }

                    return@setOnLongClickListener true
                }
            }
        } else {
            target.setOnLongClickListener {
                handleBulkActions(context, target, actions, actionLogType = LOG_LONG_CLICK)

                return@setOnLongClickListener true
            }
        }
        if (longtapActionsPassToChild) {
            target.setPenetratingLongClickable()
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
        context: BindingContext,
        target: View,
        divGestureListener: DivGestureListener,
        actions: List<DivAction>
    ) {
        if (actions.isEmpty()) {
            divGestureListener.onDoubleTapListener = null
            return
        }

        val menuAction = actions.firstOrNull { action ->
            !action.menuItems.isNullOrEmpty() && !shouldIgnoreActionMenuItems
        }
        if (menuAction != null) {
            prepareMenu(target, context, menuAction) { overflowMenuWrapper ->
                divGestureListener.onDoubleTapListener = {
                    logger.logDoubleClick(context.divView, context.expressionResolver, target, menuAction)
                    divActionBeaconSender.sendTapActionBeacon(menuAction, context.expressionResolver)
                    overflowMenuWrapper.onMenuClickListener.onClick(target)
                }
            }
        } else {
            divGestureListener.onDoubleTapListener = {
                handleBulkActions(context, target, actions, actionLogType = LOG_DOUBLE_CLICK)
            }
        }
    }

    private fun bindHoverActions(
        context: BindingContext,
        target: View,
        startActions: List<DivAction>,
        endActions: List<DivAction>
    ) {
        if (startActions.isNotEmpty() || endActions.isNotEmpty()) {
            target.setOnHoverListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_HOVER_ENTER -> {
                        handleBulkActions(context, target, startActions, LOG_HOVER)
                    }
                    MotionEvent.ACTION_HOVER_EXIT -> {
                        handleBulkActions(context, target, endActions, LOG_UNHOVER)
                    }
                }

                false
            }
        } else {
            target.setOnHoverListener(null)
        }
    }

    private fun createPressTouchListener(
        context: BindingContext,
        target: View,
        pressStartActions: List<DivAction>,
        pressEndActions: List<DivAction>
    ): ((View, MotionEvent) -> Boolean)? {
        return if (pressStartActions.isNotEmpty() || pressEndActions.isNotEmpty()) {
            { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        handleBulkActions(context, target, pressStartActions, LOG_PRESS)

                        true
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        handleBulkActions(context, target, pressEndActions, LOG_RELEASE)

                        true
                    }
                    else -> false
                }
            }
        } else {
            null
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

    internal fun handleBulkActions(
        context: BindingContext,
        target: View,
        actions: List<DivAction>,
        @LogType actionLogType: String = LOG_CLICK
    ) {
        val divView = context.divView
        val resolver = context.expressionResolver
        divView.bulkActions {
            val uuid = UUID.randomUUID().toString()

            actions.onlyEnabled(resolver).forEach { action ->
                when(actionLogType) {
                    LOG_CLICK -> logger.logClick(divView, resolver, target, action, uuid)
                    LOG_LONG_CLICK -> logger.logLongClick(divView, resolver, target, action, uuid)
                    LOG_DOUBLE_CLICK -> logger.logDoubleClick(divView, resolver, target, action, uuid)
                    LOG_FOCUS -> logger.logFocusChanged(divView, resolver, target, action, true)
                    LOG_BLUR -> logger.logFocusChanged(divView, resolver, target, action, false)
                    LOG_ENTER -> logger.logImeEnter(divView, resolver, target, action)
                    LOG_HOVER -> logger.logHoverChanged(divView, resolver, target, action, true)
                    LOG_UNHOVER -> logger.logHoverChanged(divView, resolver, target, action, false)
                    LOG_PRESS -> logger.logPressChanged(divView, resolver, target, action, true)
                    LOG_RELEASE -> logger.logPressChanged(divView, resolver, target, action, false)
                    else -> Assert.fail("Please, add new logType")
                }
                divActionBeaconSender.sendTapActionBeacon(action, resolver)
                handleActionWithoutEnableCheck(divView, resolver, action, actionLogType.toDivActionReason(), uuid)
            }
        }
    }

    private fun String.toDivActionReason() = when (this) {
        LOG_CLICK -> DivActionReason.CLICK
        LOG_LONG_CLICK -> DivActionReason.LONG_CLICK
        LOG_DOUBLE_CLICK -> DivActionReason.DOUBLE_CLICK
        LOG_FOCUS -> DivActionReason.FOCUS
        LOG_BLUR -> DivActionReason.BLUR
        LOG_ENTER -> DivActionReason.ENTER
        LOG_HOVER -> DivActionReason.HOVER
        LOG_UNHOVER -> DivActionReason.UNHOVER
        LOG_PRESS -> DivActionReason.PRESS
        LOG_RELEASE -> DivActionReason.RELEASE
        else -> DivActionReason.EXTERNAL
    }

    internal fun handleActions(
        divView: DivViewFacade,
        resolver: ExpressionResolver,
        actions: List<DivAction>?,
        reason: String,
        onEachEnabledAction: ((DivAction) -> Unit)? = null
    ) {
        if (actions == null) return
        actions.onlyEnabled(resolver).forEach {
            handleActionWithoutEnableCheck(divView, resolver, it, reason)
            onEachEnabledAction?.invoke(it)
        }
    }

    internal fun handleAction(
        divView: DivViewFacade,
        resolver: ExpressionResolver,
        action: DivAction,
        reason: String,
        actionUid: String? = null,
        viewActionHandler: DivActionHandler? = (divView as? Div2View)?.actionHandler,
    ): Boolean {
        if (!action.isEnabled.evaluate(resolver)) return false
        return handleActionWithoutEnableCheck(divView, resolver, action, reason, actionUid, viewActionHandler)
    }

    @VisibleForTesting
    internal fun handleActionWithoutEnableCheck(
        divView: DivViewFacade,
        resolver: ExpressionResolver,
        action: DivAction,
        reason: String,
        actionUid: String? = null,
        viewActionHandler: DivActionHandler? = (divView as? Div2View)?.actionHandler,
    ): Boolean {
        if (actionHandler.useActionUid && actionUid != null) {
            if (viewActionHandler?.handleActionWithReason(action, divView, resolver, actionUid, reason) == true) {
                return true
            }
            return actionHandler.handleActionWithReason(action, divView, resolver, actionUid, reason)
        } else {
            if (viewActionHandler?.handleActionWithReason(action, divView, resolver, reason) == true) {
                return true
            }
            return actionHandler.handleActionWithReason(action, divView, resolver, reason)
        }
    }

    internal fun handleTapClick(context: BindingContext, target: View, actions: List<DivAction>) {
        val resolver = context.expressionResolver
        val enabledActions = actions.onlyEnabled(resolver)
        val menuAction = enabledActions.firstOrNull { action -> !action.menuItems.isNullOrEmpty() }
        if (menuAction != null) {
            prepareMenu(target, context, menuAction) { overflowMenuWrapper ->
                logger.logClick(context.divView, resolver, target, menuAction)
                divActionBeaconSender.sendTapActionBeacon(menuAction, resolver)
                overflowMenuWrapper.onMenuClickListener.onClick(target)
            }
        } else {
            handleBulkActions(context, target, enabledActions)
        }
    }

    private inline fun prepareMenu(
        target: View,
        context: BindingContext,
        action: DivAction,
        onPrepared: (OverflowMenuWrapper) -> Unit
    ) {
        val menuItems = action.menuItems
        if (menuItems == null) {
            KAssert.fail { "Unable to bind empty menu action: ${action.logId}" }
            return
        }

        val overflowMenuWrapper = OverflowMenuWrapper(
            target.context,
            target,
            context.divView
        )
            .listener(MenuWrapperListener(context, menuItems))
            .overflowGravity(Gravity.RIGHT or Gravity.TOP)
        with(context.divView) {
            clearSubscriptions()
            subscribe { overflowMenuWrapper.dismiss() }
        }
        onPrepared(overflowMenuWrapper)
    }

    private inner class MenuWrapperListener(
        private val context: BindingContext,
        private val items: List<DivAction.MenuItem>
    ) : OverflowMenuWrapper.Listener.Simple() {

        override fun onMenuCreated(popupMenu: PopupMenu) {
            val divView = context.divView
            val expressionResolver = context.expressionResolver
            val menu = popupMenu.menu
            for (itemData in items) {
                val itemPosition = menu.size()
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
                        actions.onlyEnabled(expressionResolver).forEach { action ->
                            logger.logPopupMenuItemClick(
                                divView,
                                expressionResolver,
                                itemPosition,
                                itemData.text.evaluate(expressionResolver),
                                action
                            )
                            divActionBeaconSender.sendTapActionBeacon(action, expressionResolver)
                            handleActionWithoutEnableCheck(divView, expressionResolver, action, DivActionReason.MENU)
                        }
                        actionsHandled = true
                    }
                    actionsHandled
                }
            }
        }
    }


    @Retention(AnnotationRetention.SOURCE)
    @StringDef(LOG_CLICK, LOG_LONG_CLICK, LOG_DOUBLE_CLICK, LOG_FOCUS, LOG_BLUR, LOG_ENTER, LOG_HOVER, LOG_UNHOVER, LOG_PRESS, LOG_RELEASE)
    internal annotation class LogType {
        companion object {
            const val LOG_CLICK = "click"
            const val LOG_LONG_CLICK = "long_click"
            const val LOG_DOUBLE_CLICK = "double_click"
            const val LOG_FOCUS = "focus"
            const val LOG_BLUR = "blur"
            const val LOG_ENTER = "enter"
            const val LOG_HOVER = "hover"
            const val LOG_UNHOVER = "unhover"
            const val LOG_PRESS = "press"
            const val LOG_RELEASE = "release"
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

private fun List<DivAction>?.onlyEnabled(
    resolver: ExpressionResolver
) = this?.filter { it.isEnabled.evaluate(resolver) } ?: emptyList()

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
