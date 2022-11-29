package com.yandex.div.internal.parser

import android.graphics.Color
import android.net.Uri

interface TypeHelper<T> {

    val typeDefault: T

    fun isTypeValid(value: Any): Boolean

    companion object {
        fun <T : Any> from(default: T, validator: (Any) -> Boolean) = object : TypeHelper<T> {
            override val typeDefault: T = default

            override fun isTypeValid(value: Any): Boolean {
                return validator.invoke(value)
            }

        }
    }
}

@JvmField
val TYPE_HELPER_BOOLEAN = object : TypeHelper<Boolean> {
    override val typeDefault = false
    override fun isTypeValid(value: Any) = value is Boolean
}

@JvmField
val TYPE_HELPER_INT = object : TypeHelper<Int> {
    override val typeDefault = 0
    override fun isTypeValid(value: Any) = value is Int
}

@JvmField
val TYPE_HELPER_STRING = object : TypeHelper<String> {
    override val typeDefault = ""
    override fun isTypeValid(value: Any) = value is String
}

@JvmField
val TYPE_HELPER_DOUBLE = object : TypeHelper<Double> {
    override val typeDefault = 0f.toDouble()
    override fun isTypeValid(value: Any) = value is Double
}

@JvmField
val TYPE_HELPER_URI = object : TypeHelper<Uri> {
    override val typeDefault = Uri.EMPTY
    override fun isTypeValid(value: Any) = value is Uri
}

@JvmField
val TYPE_HELPER_COLOR = object : TypeHelper<Int> {
    override val typeDefault = Color.BLACK
    override fun isTypeValid(value: Any) = value is Int
}
