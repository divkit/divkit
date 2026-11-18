package com.yandex.div.core.util

import com.yandex.div.core.annotations.InternalApi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@InternalApi
@OptIn(ExperimentalTime::class)
public fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
