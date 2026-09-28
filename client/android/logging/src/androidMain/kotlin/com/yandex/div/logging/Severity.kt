package com.yandex.div.logging

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
