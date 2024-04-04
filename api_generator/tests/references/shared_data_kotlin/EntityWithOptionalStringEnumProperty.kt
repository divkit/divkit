// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class EntityWithOptionalStringEnumProperty(
    @JvmField final val property: Expression<Property>? = null,
) : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = 
            (property?.hashCode() ?: 0)
        _hash = hash
        return hash
    }

    companion object {
        const val TYPE = "entity_with_optional_string_enum_property"
    }


    enum class Property(private val value: String) {
        FIRST("first"),
        SECOND("second");

        companion object Converter {
            fun toString(obj: Property): String {
                return obj.value
            }

            fun fromString(string: String): Property? {
                return when (string) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    else -> null
                }
            }

            val FROM_STRING = { string: String ->
                when (string) {
                    FIRST.value -> FIRST
                    SECOND.value -> SECOND
                    else -> null
                }
            }
        }
    }
}
