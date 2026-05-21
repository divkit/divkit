package com.yandex.div.compose.actions

import com.yandex.div.core.annotations.ExperimentalApi

/**
 * Source of DivKit action.
 */
@ExperimentalApi
enum class DivActionSource {
    DISAPPEAR,
    DOUBLE_TAP,
    EXTERNAL,
    LONG_TAP,
    TAP,
    TRIGGER,
    VISIBILITY
}
