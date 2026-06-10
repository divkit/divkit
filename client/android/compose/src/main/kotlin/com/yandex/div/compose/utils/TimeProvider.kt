package com.yandex.div.compose.utils

// TODO: replace with kotlin.time.Clock after Java/Kotlin upgrade.
internal interface TimeProvider {
    val currentTimeMillis: Long
}

internal class SystemTimeProvider() : TimeProvider {
    override val currentTimeMillis: Long
        get() = System.currentTimeMillis()
}
