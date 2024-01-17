// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

sealed class EnumWithDefaultType : Hashable {
    class WithDefaultCase(val value: WithDefault) : EnumWithDefaultType()
    class WithoutDefaultCase(val value: WithoutDefault) : EnumWithDefaultType()

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        return when(this) {
            is WithDefaultCase -> 31 + this.value.hash()
            is WithoutDefaultCase -> 62 + this.value.hash()
        }.also {
            _hash = it
        }
    }

    fun value(): Any {
        return when (this) {
            is WithDefaultCase -> value
            is WithoutDefaultCase -> value
        }
    }

}
