package com.yandex.div.core.view2

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.accessibility.AccessibilityNodeProviderCompat

internal class AccessibilityDelegateWrapper(
    private val originalDelegate: AccessibilityDelegateCompat?,

    var initializeAccessibilityNodeInfo:
        (host: View?, info: AccessibilityNodeInfoCompat?) -> Unit = { _, _ -> },

    var actionsAccessibilityNodeInfo:
        (host: View?, info: AccessibilityNodeInfoCompat?) -> Unit = { _, _ -> },

) : AccessibilityDelegateCompat() {

    override fun sendAccessibilityEvent(host: View?, eventType: Int) {
        originalDelegate?.sendAccessibilityEvent(host, eventType)
            ?: super.sendAccessibilityEvent(host, eventType)
    }

    override fun sendAccessibilityEventUnchecked(host: View?, event: AccessibilityEvent?) {
        originalDelegate?.sendAccessibilityEventUnchecked(host, event)
            ?: super.sendAccessibilityEventUnchecked(host, event)
    }

    override fun dispatchPopulateAccessibilityEvent(
        host: View?,
        event: AccessibilityEvent?
    ): Boolean {
        return originalDelegate?.dispatchPopulateAccessibilityEvent(host, event)
            ?: super.dispatchPopulateAccessibilityEvent(host, event)
    }

    override fun onPopulateAccessibilityEvent(host: View?, event: AccessibilityEvent?) {
        originalDelegate?.onPopulateAccessibilityEvent(host, event)
            ?: super.onPopulateAccessibilityEvent(host, event)
    }

    override fun onInitializeAccessibilityEvent(host: View?, event: AccessibilityEvent?) {
        originalDelegate?.onInitializeAccessibilityEvent(host, event)
            ?: super.onInitializeAccessibilityEvent(host, event)
    }

    override fun onInitializeAccessibilityNodeInfo(
        host: View?,
        info: AccessibilityNodeInfoCompat?
    ) {
        originalDelegate?.onInitializeAccessibilityNodeInfo(host, info)
            ?: super.onInitializeAccessibilityNodeInfo(host, info)

        initializeAccessibilityNodeInfo(host, info)

        actionsAccessibilityNodeInfo(host, info)
    }

    override fun onRequestSendAccessibilityEvent(
        host: ViewGroup?,
        child: View?,
        event: AccessibilityEvent?
    ): Boolean {
        return originalDelegate?.onRequestSendAccessibilityEvent(host, child, event)
            ?: super.onRequestSendAccessibilityEvent(host, child, event)
    }

    override fun getAccessibilityNodeProvider(host: View?): AccessibilityNodeProviderCompat? {
        return originalDelegate?.getAccessibilityNodeProvider(host)
            ?: super.getAccessibilityNodeProvider(host)
    }

    override fun performAccessibilityAction(host: View?, action: Int, args: Bundle?): Boolean {
        return originalDelegate?.performAccessibilityAction(host, action, args)
            ?: super.performAccessibilityAction(host, action, args)
    }
}
