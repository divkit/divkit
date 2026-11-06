package com.yandex.div.internal.widget

import androidx.core.view.accessibility.AccessibilityNodeInfoCompat.AccessibilityActionCompat

internal class AccessibilityMenuAction(
    val info: AccessibilityActionCompat,
    val isAvailable: () -> Boolean,
    val perform: () -> Boolean,
)
