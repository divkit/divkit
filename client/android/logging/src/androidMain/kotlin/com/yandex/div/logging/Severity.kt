package com.yandex.div.logging

import com.yandex.div.core.log.Severity as CoreSeverity

public enum class Severity {
    ERROR,
    WARNING,
    INFO,
    DEBUG,
    VERBOSE;

    public fun isAtLeast(minLevel: Severity): Boolean {
        return this.ordinal >= minLevel.ordinal
    }
}

internal fun Severity.toCoreSeverity(): CoreSeverity {
    return when (this) {
        Severity.ERROR -> CoreSeverity.ERROR
        Severity.WARNING -> CoreSeverity.WARNING
        Severity.INFO -> CoreSeverity.INFO
        Severity.DEBUG -> CoreSeverity.DEBUG
        Severity.VERBOSE -> CoreSeverity.VERBOSE
    }
}

internal fun CoreSeverity.toSeverity(): Severity {
    return when (this) {
        CoreSeverity.ERROR -> Severity.ERROR
        CoreSeverity.WARNING -> Severity.WARNING
        CoreSeverity.INFO -> Severity.INFO
        CoreSeverity.DEBUG -> Severity.DEBUG
        CoreSeverity.VERBOSE -> Severity.VERBOSE
    }
}

internal fun Severity.toLogPriority(): Int {
    return when (this) {
        Severity.ERROR -> android.util.Log.ERROR
        Severity.WARNING -> android.util.Log.WARN
        Severity.INFO -> android.util.Log.INFO
        Severity.DEBUG -> android.util.Log.DEBUG
        Severity.VERBOSE -> android.util.Log.VERBOSE
    }
}

internal fun Int.toCoreSeverity(): CoreSeverity {
    return when (this) {
        android.util.Log.ASSERT -> CoreSeverity.ERROR
        android.util.Log.ERROR -> CoreSeverity.ERROR
        android.util.Log.WARN -> CoreSeverity.WARNING
        android.util.Log.INFO -> CoreSeverity.INFO
        android.util.Log.DEBUG -> CoreSeverity.DEBUG
        android.util.Log.VERBOSE -> CoreSeverity.VERBOSE
        else -> throw IllegalArgumentException("Unknown Log priority: $this")
    }
}
