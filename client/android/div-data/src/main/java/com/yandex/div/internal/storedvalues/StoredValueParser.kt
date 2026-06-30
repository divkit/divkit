package com.yandex.div.internal.storedvalues

import com.yandex.div.core.annotations.InternalApi
import com.yandex.div.data.StoredValue
import com.yandex.div.data.StoredValue.*
import com.yandex.div.evaluable.types.Color
import com.yandex.div.evaluable.types.Url
import com.yandex.div.internal.util.toBoolean
import kotlin.text.toBooleanStrictOrNull
import kotlin.text.toLong

@InternalApi
class StoredValueParser(
    private val reportError: (String) -> Unit
) {

    fun parse(
        type: Type,
        name: String,
        value: String,
    ): StoredValue? {
        return when (type) {
            Type.STRING -> StringStoredValue(name, value)

            Type.BOOLEAN -> {
                val booleanValue = try {
                    value.toBooleanStrictOrNull() ?: value.toInt().toBoolean()
                } catch (_: NumberFormatException) {
                    null
                }
                if (booleanValue == null) {
                    reportError("Failed to parse stored value: $value")
                    null
                } else {
                    BooleanStoredValue(name, booleanValue)
                }
            }

            Type.COLOR ->
                try {
                    ColorStoredValue(name, Color.parse(value))
                } catch (_: IllegalArgumentException) {
                    reportError("Failed to parse stored value: $value")
                    null
                }

            Type.INTEGER -> {
                try {
                    IntegerStoredValue(name, value.toLong())
                } catch (_: NumberFormatException) {
                    reportError("Failed to parse stored value: $value")
                    null
                }
            }

            Type.NUMBER -> {
                try {
                    DoubleStoredValue(name, value.toDouble())
                } catch (_: NumberFormatException) {
                    reportError("Failed to parse stored value: $value")
                    null
                }
            }

            Type.URL -> {
                try {
                    UrlStoredValue(name, Url.from(value))
                } catch (_: IllegalArgumentException) {
                    reportError("Failed to parse stored value: $value")
                    null
                }
            }

            Type.ARRAY, Type.DICT -> {
                reportError("Invalid stored value type: $type")
                null
            }
        }
    }
}
