package com.yandex.div.core.view2.divs

import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.ScaleAnimation
import androidx.annotation.StringDef
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import com.yandex.div.R
import com.yandex.div.core.Div2Logger
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.animation.reversed
import com.yandex.div.core.annotations.Mockable
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.dagger.ExperimentFlag
import com.yandex.div.core.experiments.Experiment.ACCESSIBILITY_ENABLED
import com.yandex.div.core.experiments.Experiment.IGNORE_ACTION_MENU_ITEMS_ENABLED
import com.yandex.div.core.experiments.Experiment.LONGTAP_ACTIONS_PASS_TO_CHILD_ENABLED
import com.yandex.div.core.util.androidInterpolator
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivGestureListener
import com.yandex.div.core.view2.divs.DivActionBinder.LogType.Companion.LOG_BLUR
import com.yandex.div.core.view2.divs.DivActionBinder.LogType.Companion.LOG_CLICK
import com.yandex.div.core.view2.divs.DivActionBinder.LogType.Companion.LOG_DOUBLE_CLICK
import com.yandex.div.core.view2.divs.DivActionBinder.LogType.Companion.LOG_FOCUS
import com.yandex.div.core.view2.divs.DivActionBinder.LogType.Companion.LOG_LONG_CLICK
import com.yandex.div.internal.Assert
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.util.allIsNullOrEmpty
import com.yandex.div.internal.widget.menu.OverflowMenuWrapper
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAccessibility
import com.yandex.div2.DivAction
import com.yandex.div2.DivAnimation
import java.util.*
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
        divView: Div2View,
        target: View,
        actions: List<DivAction>?,
        longTapActions: List<DivAction>?,
        doubleTapActions: List<DivAction>?,
        actionAnimation: DivAnimation
    ) {
        val clickableState = target.isClickable
        val longClickableState = target.isLongClickable

        val touchAnimations = tryConvertToTouchListener(
            divView, actions, longTapActions, doubleTapActions, actionAnimation, target)

        val divGestureListener = DivGestureListener()
        bindLongTapActions(divView, target, longTapActions, actions.isNullOrEmpty())
        bindDoubleTapActions(divView, target, divGestureListener, doubleTapActions)
        // Order is urgent: tap actions depend on double tap actions
        bindTapActions(divView, target, divGestureListener, actions, shouldIgnoreActionMenuItems)

        // Avoid creating GestureDetector if unnecessary cause it's expensive.
        val gestureDetector = if (divGestureListener.onSingleTapListener != null ||
            divGestureListener.onDoubleTapListener != null) {
            GestureDetectorCompat(target.context, divGestureListener)
        } else {
            null
        }

        if (touchAnimations != null || gestureDetector != null) {
            //noinspection ClickableViewAccessibility
            target.setOnTouchListener { v, event ->
                touchAnimations?.invoke(v, event)
                gestureDetector?.onTouchEvent(event) ?: false
            }
        } else {
            target.setOnTouchListener(null)
        }

        if (accessibilityEnabled &&
            DivAccessibility.Mode.MERGE == divView.getPropagatedAccessibilityMode(target) &&
            divView.isDescendantAccessibilityMode(target)) {
            target.isClickable = clickableState
            target.isLongClickable = longClickableState
        }
    }

    private fun bindTapActions(
        divView: Div2View,
        target: View,
        divGestureListener: DivGestureListener,
        actions: List<DivAction>?,
        shouldIgnoreActionMenuItems: Boolean
    ) {
        if (actions.isNullOrEmpty()) {
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
            prepareMenu(target, divView, menuAction) { overflowMenuWrapper ->
                setTapListener {
                    logger.logClick(divView, target, menuAction)
                    divActionBeaconSender.sendTapActionBeacon(menuAction,
                        divView.expressionResolver)
                    overflowMenuWrapper.onMenuClickListener.onClick(target)
                }
            }
        } else {
            setTapListener {
                handleBulkActions(divView, target, actions)
            }
        }

    }

    private fun bindLongTapActions(
        divView: Div2View,
        target: View,
        actions: List<DivAction>?,
        noClickAction: Boolean
    ) {
        if (actions.isNullOrEmpty()) {
            clearLongClickListener(target, longtapActionsPassToChild, noClickAction)
            return
        }

        val menuAction = actions.firstOrNull { action ->
            !action.menuItems.isNullOrEmpty() && !shouldIgnoreActionMenuItems
        }
        if (menuAction != null) {
            prepareMenu(target, divView, menuAction) { overflowMenuWrapper ->
                target.setOnLongClickListener {
                    val uuid = UUID.randomUUID().toString()

                    divActionBeaconSender.sendTapActionBeacon(menuAction,
                        divView.expressionResolver)
                    overflowMenuWrapper.onMenuClickListener.onClick(target)
                    actions.forEach { action -> logger.logLongClick(divView, target, action, uuid) }

                    return@setOnLongClickListener true
                }
            }
        } else {
            target.setOnLongClickListener {
                handleBulkActions(divView, target, actions, actionLogType = LOG_LONG_CLICK)

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
        divView: Div2View,
        target: View,
        divGestureListener: DivGestureListener,
        actions: List<DivAction>?
    ) {
        if (actions.isNullOrEmpty()) {
            divGestureListener.onDoubleTapListener = null
            return
        }

        val menuAction = actions.firstOrNull { action ->
            !action.menuItems.isNullOrEmpty() && !shouldIgnoreActionMenuItems
        }
        if (menuAction != null) {
            prepareMenu(target, divView, menuAction) { overflowMenuWrapper ->
                divGestureListener.onDoubleTapListener = {
                    logger.logDoubleClick(divView, target, menuAction)
                    divActionBeaconSender.sendTapActionBeacon(menuAction,
                        divView.expressionResolver)
                    overflowMenuWrapper.onMenuClickListener.onClick(target)
                }
            }
        } else {
            divGestureListener.onDoubleTapListener = {
                handleBulkActions(divView, target, actions, actionLogType = LOG_DOUBLE_CLICK)
            }
        }
    }

    internal fun handleBulkActions(
        divView: Div2View,
        target: View,
        actions: List<DivAction>,
        @LogType actionLogType: String = LOG_CLICK
    ) {
        divView.bulkActions {
            val uuid = UUID.randomUUID().toString()

            actions.forEach { action ->
                when(actionLogType) {
                    LOG_CLICK -> logger.logClick(divView, target, action, uuid)
                    LOG_LONG_CLICK -> logger.logLongClick(divView, target, action, uuid)
                    LOG_DOUBLE_CLICK -> logger.logDoubleClick(divView, target, action, uuid)
                    LOG_FOCUS -> logger.logFocusChanged(divView, target, action, true)
                    LOG_BLUR -> logger.logFocusChanged(divView, target, action, false)
                    else -> Assert.fail("Please, add new logType")
                }
                divActionBeaconSender.sendTapActionBeacon(action,
                    divView.expressionResolver)
                handleAction(divView, action, uuid)
            }
        }
    }

    internal fun handleAction(divView: Div2View, action: DivAction, actionUid: String? = null) {
        val viewActionHandler = divView.actionHandler
        if (actionHandler.useActionUid && actionUid != null) {
            if (viewActionHandler == null || !viewActionHandler.handleAction(action, divView, actionUid)) {
                actionHandler.handleAction(action, divView, actionUid)
            }
        } else {
            if (viewActionHandler == null || !viewActionHandler.handleAction(action, divView)) {
                actionHandler.handleAction(action, divView)
            }
        }
    }

    internal fun handleTapClick(divView: Div2View, target: View, actions: List<DivAction>) {
        val menuAction = actions.firstOrNull { action -> !action.menuItems.isNullOrEmpty() }
        if (menuAction != null) {
            prepareMenu(target, divView, menuAction) { overflowMenuWrapper ->
                logger.logClick(divView, target, menuAction)
                divActionBeaconSender.sendTapActionBeacon(menuAction,
                    divView.expressionResolver)
                overflowMenuWrapper.onMenuClickListener.onClick(target)
            }
        } else {
            handleBulkActions(divView, target, actions)
        }
    }

    private inline fun prepareMenu(
        target: View,
        divView: Div2View,
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
            divView
        )
            .listener(MenuWrapperListener(divView, menuItems))
            .overflowGravity(Gravity.RIGHT or Gravity.TOP)
        with(divView) {
            clearSubscriptions()
            subscribe { overflowMenuWrapper.dismiss() }
        }
        onPrepared(overflowMenuWrapper)
    }

    private inner class MenuWrapperListener(
        private val divView: Div2View,
        private val items: List<DivAction.MenuItem>
    ) : OverflowMenuWrapper.Listener.Simple() {

        override fun onMenuCreated(popupMenu: PopupMenu) {
            val expressionResolver = divView.expressionResolver
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
                        actions.forEach { action ->
                            logger.logPopupMenuItemClick(
                                divView,
                                itemPosition,
                                itemData.text.evaluate(expressionResolver),
                                action
                            )
                            divActionBeaconSender.sendTapActionBeacon(action,
                                divView.expressionResolver)
                            handleAction(divView, action)
                        }
                        actionsHandled = true
                    }
                    actionsHandled
                }
            }
        }
    }

    private fun tryConvertToTouchListener(
        divView: Div2View,
        actions: List<DivAction>?,
        longTapActions: List<DivAction>?,
        doubleTapActions: List<DivAction>?,
        actionAnimation: DivAnimation,
        view: View,
    ): ((View, MotionEvent) -> Unit)? {
        val expressionResolver = divView.expressionResolver

        if (allIsNullOrEmpty(actions, longTapActions, doubleTapActions)) {
            return null
        }

        val directAnimation = actionAnimation.toAnimation(expressionResolver, view = view)
        val reverseAnimation = actionAnimation.toAnimation(expressionResolver, reverse = true)

        if (directAnimation == null && reverseAnimation == null) {
            return null
        }

        return { v, event ->
            if (v.isEnabled && v.isClickable && v.hasOnClickListeners()) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> directAnimation?.let { v.startAnimation(it) }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> reverseAnimation?.let {
                        v.startAnimation(it)
                    }
                }
            }
        }
    }

    private fun DivAnimation.toAnimation(
        resolver: ExpressionResolver,
        reverse: Boolean = false,
        view: View? = null
    ): Animation? {
        val animationName = name.evaluate(resolver)
        val animation = when (animationName) {
            DivAnimation.Name.SET -> AnimationSet(false).apply {
                items?.forEach { divAnimation ->
                    val animation = divAnimation.toAnimation(resolver, reverse, view)
                    animation?.let { addAnimation(it) }
                }
            }
            DivAnimation.Name.SCALE ->
                if (reverse) {
                    createScaleAnimation(
                        endValue?.evaluate(resolver).scaleValue() ?: DEFAULT_SCALE_END_VALUE,
                        startValue?.evaluate(resolver).scaleValue() ?: DEFAULT_SCALE_START_VALUE
                    )
                } else {
                    createScaleAnimation(
                        startValue?.evaluate(resolver).scaleValue() ?: DEFAULT_SCALE_START_VALUE,
                        endValue?.evaluate(resolver).scaleValue() ?: DEFAULT_SCALE_END_VALUE
                    )
                }
            DivAnimation.Name.NATIVE -> {
                if (view != null) {
                    val animation = ContextCompat
                        .getDrawable(view.context, R.drawable.native_animation_background)
                    val drawables = mutableListOf<Drawable>()

                    if (view.background is LayerDrawable) {
                        val layers = view.background as LayerDrawable
                        for (i in 0 until layers.numberOfLayers) {
                            drawables.add(layers.getDrawable(i))
                        }
                    } else {
                        drawables.add(view.background)
                    }

                    animation?.let { drawables.add(it) }

                    val layerDrawable = LayerDrawable(drawables.toTypedArray())
                    layerDrawable.setId(
                        drawables.size - 1,
                        R.drawable.native_animation_background
                    ) //mark background has animation
                    view.background = layerDrawable
                }
                null
            }
            DivAnimation.Name.NO_ANIMATION -> null
            else ->
                if (reverse) {
                    AlphaAnimation(
                        endValue?.evaluate(resolver).alphaValue() ?: DEFAULT_ALPHA_END_VALUE,
                        startValue?.evaluate(resolver).alphaValue() ?: DEFAULT_ALPHA_START_VALUE
                    )
                } else {
                    AlphaAnimation(
                        startValue?.evaluate(resolver).alphaValue() ?: DEFAULT_ALPHA_START_VALUE,
                        endValue?.evaluate(resolver).alphaValue() ?: DEFAULT_ALPHA_END_VALUE
                    )
                }
        }

        if (animationName != DivAnimation.Name.SET) {
            animation?.interpolator = if (reverse) {
                interpolator.evaluate(resolver).androidInterpolator.reversed()
            } else {
                interpolator.evaluate(resolver).androidInterpolator
            }
            animation?.duration = duration.evaluate(resolver).toLong()
        }

        animation?.startOffset = startDelay.evaluate(resolver).toLong()
        animation?.fillAfter = true

        return animation
    }

    private fun createScaleAnimation(startValue: Float, endValue: Float) =
        ScaleAnimation(
            startValue, endValue, startValue, endValue,
            Animation.RELATIVE_TO_SELF, SCALE_PIVOT_VALUE,
            Animation.RELATIVE_TO_SELF, SCALE_PIVOT_VALUE
        )

    private companion object {
        const val MIN_ALPHA_VALUE = 0.0f
        const val MAX_ALPHA_VALUE = 1.0f
        const val DEFAULT_ALPHA_START_VALUE = 1f
        const val DEFAULT_ALPHA_END_VALUE = 0.6f

        const val MIN_SCALE_VALUE = 0.0f
        const val DEFAULT_SCALE_START_VALUE = 1f
        const val DEFAULT_SCALE_END_VALUE = 0.95f
        const val SCALE_PIVOT_VALUE = 0.5f

        private fun Double?.alphaValue(): Float? {
            return this?.toFloat()?.coerceIn(MIN_ALPHA_VALUE, MAX_ALPHA_VALUE)
        }

        private fun Double?.scaleValue(): Float? {
            return this?.toFloat()?.coerceAtLeast(MIN_SCALE_VALUE)
        }
    }


    @Retention(AnnotationRetention.SOURCE)
    @StringDef(LOG_CLICK, LOG_LONG_CLICK, LOG_DOUBLE_CLICK, LOG_FOCUS, LOG_BLUR)
    internal annotation class LogType {
        companion object {
            const val LOG_CLICK = "click"
            const val LOG_LONG_CLICK = "long_click"
            const val LOG_DOUBLE_CLICK = "double_click"
            const val LOG_FOCUS = "focus"
            const val LOG_BLUR = "blur"
        }
    }
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
