package com.yandex.div.logging

enum class Severity {
    ERROR,
    WARNING,
    INFO,
    DEBUG,
    VERBOSE;

    fun isAtLeast(minLevel: Severity): Boolean {
        return this.ordinal >= minLevel.ordinal
    }
}
