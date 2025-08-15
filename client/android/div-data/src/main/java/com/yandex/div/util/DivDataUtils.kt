package com.yandex.div.util

import com.yandex.div2.DivData

val DivData.Companion.INVALID_STATE_ID get() = -1L

fun DivData.getInitialStateId(): Long =
    if (states.isEmpty()) DivData.INVALID_STATE_ID else states[0].stateId
