package com.yandex.div.core.actions

import android.view.View
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS
import androidx.core.content.ContextCompat
import com.yandex.div.core.util.doOnActualLayout
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.ViewLocator
import com.yandex.div.core.view2.divs.widgets.DivInputView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivActionFocusElement
import com.yandex.div2.DivActionTyped
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DivActionTypedFocusElementHandler @Inject constructor() : DivActionTypedHandler {

    override fun handleAction(
        scopeId: String?,
        action: DivActionTyped,
        view: Div2View,
        resolver: ExpressionResolver
    ): Boolean {
        return when (action) {
            is DivActionTyped.FocusElement -> {
                handleRequestFocus(action.value, scopeId, view, resolver)
                true
            }

            else -> false
        }
    }

    private fun handleRequestFocus(
        action: DivActionFocusElement,
        scopeId: String?,
        view: Div2View,
        resolver: ExpressionResolver
    ) {
        val elementId = action.elementId.evaluate(resolver)
        val requestedView = ViewLocator.findSingleViewWithTag<View>(view, elementId, scopeId)
            .onFailure {
                if (it is ViewLocator.DuplicateTarget) return view.logActionError(DivActionFocusElement.TYPE, it)
            }.getOrNull()
            ?: view.viewComponent.divTooltipController.findViewWithTag(elementId, scopeId)
            ?: return

        requestedView.requestFocus()
        when (requestedView) {
            is DivInputView -> requestedView.openKeyboard()
        }
        requestedView.requestScreenReaderFocus()
    }

    private fun View.requestScreenReaderFocus() {
        val manager = ContextCompat.getSystemService(context, AccessibilityManager::class.java)
        if (manager?.isTouchExplorationEnabled != true) return

        doOnActualLayout {
            if (it.isAttachedToWindow && it.isShown && it.isImportantForAccessibility) {
                it.performAccessibilityAction(ACTION_ACCESSIBILITY_FOCUS, null)
            }
        }
    }
}
