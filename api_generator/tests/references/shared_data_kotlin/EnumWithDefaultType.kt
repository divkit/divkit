// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

sealed class EnumWithDefaultType {
    class WithDefaultCase(val value: WithDefault) : EnumWithDefaultType()
    class WithoutDefaultCase(val value: WithoutDefault) : EnumWithDefaultType()

    fun value(): Any {
        return when (this) {
            is WithDefaultCase -> value
            is WithoutDefaultCase -> value
        }
    }

}
