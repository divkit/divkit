package com.yandex.div.core.view2

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.core.view.accessibility.AccessibilityNodeProviderCompat
import com.yandex.div.internal.widget.AccessibilityMenuAction

internal class AccessibilityMenuDelegate(
    val original: AccessibilityDelegateCompat,
) : AccessibilityDelegateCompat() {
    var commands: List<AccessibilityMenuAction> = emptyList()

    override fun onInitializeAccessibilityNodeInfo(host: View, info: AccessibilityNodeInfoCompat) {
        original.onInitializeAccessibilityNodeInfo(host, info)
        commands.forEach {
            if (it.isAvailable()) {
                info.addAction(it.info)
            }
        }
    }

    override fun performAccessibilityAction(host: View, action: Int, args: Bundle?): Boolean {
        val command: AccessibilityMenuAction? = commands.firstOrNull { it.info.id == action }
        return command?.perform?.invoke() ?: original.performAccessibilityAction(host, action, args)
    }

    override fun sendAccessibilityEvent(host: View, eventType: Int) =
        original.sendAccessibilityEvent(host, eventType)

    override fun sendAccessibilityEventUnchecked(host: View, event: AccessibilityEvent) =
        original.sendAccessibilityEventUnchecked(host, event)

    override fun dispatchPopulateAccessibilityEvent(host: View, event: AccessibilityEvent): Boolean =
        original.dispatchPopulateAccessibilityEvent(host, event)

    override fun onPopulateAccessibilityEvent(host: View, event: AccessibilityEvent) =
        original.onPopulateAccessibilityEvent(host, event)

    override fun onInitializeAccessibilityEvent(host: View, event: AccessibilityEvent) =
        original.onInitializeAccessibilityEvent(host, event)

    override fun onRequestSendAccessibilityEvent(
        host: ViewGroup,
        child: View,
        event: AccessibilityEvent,
    ): Boolean = original.onRequestSendAccessibilityEvent(host, child, event)

    override fun getAccessibilityNodeProvider(host: View): AccessibilityNodeProviderCompat? =
        original.getAccessibilityNodeProvider(host)
}
