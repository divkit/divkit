// Generated code. Do not modify.

package com.yandex.div2

import org.json.JSONObject

class WithDefault() : Hashable {

    private var _hash: Int? = null 

    override fun hash(): Int {
        _hash?.let {
            return it
        }
        val hash = javaClass.hashCode()
        _hash = hash
        return hash
    }

    companion object {
        const val TYPE = "default"
    }

}
