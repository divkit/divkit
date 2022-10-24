package com.yandex.div.core.view2.accessibility

import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

internal fun AccessibilityDelegateCompat?.addActionClickHint(
    hint: String
): AccessibilityDelegateCompat {
    return AccessibilityDelegateWrapper(
        originalDelegate = this,
        initializeAccessibilityNodeInfo = { _, info ->
            info?.addAction(
                AccessibilityNodeInfoCompat.AccessibilityActionCompat(
                    AccessibilityNodeInfo.ACTION_CLICK,
                    hint
                )
            )
        }
    )
}
