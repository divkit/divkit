package com.yandex.div.dsl.type

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.yandex.div.dsl.serializer.ColorSerializer

private val argbPattern = Regex("#([\\dA-Fa-f]{3}|[\\dA-Fa-f]{4}|[\\dA-Fa-f]{6}|[\\dA-Fa-f]{8})")

@JsonSerialize(using = ColorSerializer::class)
class Color(val argbValue: String) {

    init {
        require(argbPattern.matchEntire(argbValue) != null) {
            "Malformed color string: $argbValue"
        }
    }

    companion object {
        @JvmStatic val TRANSPARENT = "#00000000".color
    }
}

val String.color
    get() = Color(this)
