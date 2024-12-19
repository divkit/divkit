package com.yandex.div.datetime.utils

import com.yandex.div.datetime.domain.PickerType
import com.yandex.div.datetime.data.PickerMode

private const val MODE_DELIMETER = "|"

internal fun parseMode(mode: String): Set<PickerMode> {
    return mode
        .split(MODE_DELIMETER)
        .mapNotNull(PickerMode::fromModeName)
        .toSet()
}

internal fun Set<PickerMode>.toPickerType() = when {
    PickerMode.DATE in this && PickerMode.TIME in this -> PickerType.DATE_TIME
    PickerMode.DATE in this -> PickerType.DATE
    PickerMode.TIME in this -> PickerType.TIME
    else -> null
}