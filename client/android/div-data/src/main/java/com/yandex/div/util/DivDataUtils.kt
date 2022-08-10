package com.yandex.div.util

import com.yandex.div2.DivData

object DivDataUtils {
    const val INVALID_STATE_ID = -1

    @JvmStatic
    fun DivData.getInitialStateId(): Int =
        if (states.isEmpty()) INVALID_STATE_ID else states[0].stateId
}