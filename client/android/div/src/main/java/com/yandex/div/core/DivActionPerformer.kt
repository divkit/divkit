package com.yandex.div.core

import android.view.View
import androidx.annotation.StringDef
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.divs.DivActionBeaconSender
import com.yandex.div.internal.Assert
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivAction
import java.util.UUID
import javax.inject.Inject

@DivScope
internal class DivActionPerformer @Inject constructor(
    private val actionHandler: DivActionHandler,
    private val logger: Div2Logger,
    private val divActionBeaconSender: DivActionBeaconSender,
) {

    fun performBulkActions(
        context: BindingContext,
        target: View,
        actions: List<DivAction>,
        @LogType actionLogType: String = DivActionReason.CLICK
    ) {
        context.divView.bulkActions {
            val uuid = UUID.randomUUID().toString()
            actions.forEach { performActionWithLogging(context, target, it, actionLogType, uuid) }
        }
    }

    private fun performActionWithLogging(
        context: BindingContext,
        target: View,
        action: DivAction,
        @LogType actionLogType: String,
        uuid: String,
    ) {
        val divView = context.divView
        val resolver = context.expressionResolver
        if (!action.isEnabled.evaluate(resolver)) return

        when(actionLogType) {
            DivActionReason.CLICK -> logger.logClick(divView, resolver, target, action, uuid)
            DivActionReason.LONG_CLICK -> logger.logLongClick(divView, resolver, target, action, uuid)
            DivActionReason.DOUBLE_CLICK -> logger.logDoubleClick(divView, resolver, target, action, uuid)
            DivActionReason.FOCUS -> logger.logFocusChanged(divView, resolver, target, action, true)
            DivActionReason.BLUR -> logger.logFocusChanged(divView, resolver, target, action, false)
            DivActionReason.ENTER -> logger.logImeEnter(divView, resolver, target, action)
            DivActionReason.HOVER -> logger.logHoverChanged(divView, resolver, target, action, true)
            DivActionReason.UNHOVER -> logger.logHoverChanged(divView, resolver, target, action, false)
            DivActionReason.PRESS -> logger.logPressChanged(divView, resolver, target, action, true)
            DivActionReason.RELEASE -> logger.logPressChanged(divView, resolver, target, action, false)
            else -> Assert.fail("Please, add new logType")
        }

        divActionBeaconSender.sendTapActionBeacon(action, resolver)
        performActionWithoutEnableCheck(divView, resolver, action, actionLogType, uuid)
    }

    fun performActions(
        divView: Div2View,
        resolver: ExpressionResolver,
        actions: List<DivAction>?,
        reason: String,
        actionUid: String? = null,
    ) {
        actions?.forEach {
            performAction(divView, resolver, it, reason, actionUid)
        }
    }

    fun performAction(
        divView: Div2View,
        resolver: ExpressionResolver,
        action: DivAction,
        reason: String,
        actionUid: String? = null,
        viewActionHandler: DivActionHandler? = divView.actionHandler,
    ): Boolean {
        if (!action.isEnabled.evaluate(resolver)) return false
        return performActionWithoutEnableCheck(divView, resolver, action, reason, actionUid, viewActionHandler)
    }

    private fun performActionWithoutEnableCheck(
        divView: Div2View,
        resolver: ExpressionResolver,
        action: DivAction,
        reason: String,
        actionUid: String? = null,
        viewActionHandler: DivActionHandler? = divView.actionHandler,
    ): Boolean {
        return when {
            actionHandler.useActionUid && actionUid != null -> {
                viewActionHandler?.handleActionWithReason(action, divView, resolver, actionUid, reason) == true ||
                    actionHandler.handleActionWithReason(action, divView, resolver, actionUid, reason)
            }

            viewActionHandler?.handleActionWithReason(action, divView, resolver, reason) == true -> true
            else -> actionHandler.handleActionWithReason(action, divView, resolver, reason)
        }
    }

    fun performTriggerActions(divView: Div2View, resolver: ExpressionResolver, actions: List<DivAction>) {
        actions.forEach {
            if (!it.isEnabled.evaluate(resolver)) return@forEach

            logger.logTrigger(divView, it)
            performActionWithoutEnableCheck(divView, resolver, it, DivActionReason.TRIGGER)
        }
    }

    fun performSwipeOutActions(
        divView: Div2View,
        resolver: ExpressionResolver,
        target: View,
        actions: List<DivAction>?
    ) {
        divView.bulkActions {
            actions?.forEach {
                if (!it.isEnabled.evaluate(resolver)) return@forEach
                performActionWithoutEnableCheck(divView, resolver, it, DivActionReason.STATE_SWIPE_OUT)
                logger.logSwipedAway(divView, resolver, target, it)
                divActionBeaconSender.sendSwipeOutActionBeacon(it, resolver)
            }
        }
    }

    fun performTabTitleClick(
        divView: Div2View,
        resolver: ExpressionResolver,
        action: DivAction,
        tabPosition: Int,
    ) {
        logger.logActiveTabTitleClick(divView, resolver, tabPosition, action)
        performAction(divView, resolver, action, DivActionReason.CLICK)
    }

    fun performMenuActions(
        divView: Div2View,
        resolver: ExpressionResolver,
        actions: List<DivAction>,
        itemPosition: Int,
        itemData: DivAction.MenuItem,
    ) {
        actions.forEach { action ->
            if (!action.isEnabled.evaluate(resolver)) return@forEach

            logger.logPopupMenuItemClick(
                divView,
                resolver,
                itemPosition,
                itemData.text.evaluate(resolver),
                action
            )

            divActionBeaconSender.sendTapActionBeacon(action, resolver)
            performActionWithoutEnableCheck(divView, resolver, action, DivActionReason.MENU)
        }
    }

    @Retention(AnnotationRetention.SOURCE)
    @StringDef(
        DivActionReason.CLICK,
        DivActionReason.LONG_CLICK,
        DivActionReason.DOUBLE_CLICK,
        DivActionReason.FOCUS,
        DivActionReason.BLUR,
        DivActionReason.ENTER,
        DivActionReason.HOVER,
        DivActionReason.UNHOVER,
        DivActionReason.PRESS,
        DivActionReason.RELEASE
    )
    annotation class LogType
}
