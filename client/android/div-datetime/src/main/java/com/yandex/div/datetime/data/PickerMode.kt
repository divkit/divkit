package com.yandex.div.datetime.data

internal enum class PickerMode(val modeName: String) {
    DATE("date"),
    TIME("time");

    companion object {

        fun fromModeName(mode: String): PickerMode? = when (mode) {
            DATE.modeName -> DATE
            TIME.modeName -> TIME
            else -> null
        }
    }
}