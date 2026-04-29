package com.yandex.div.evaluable

interface ScopedStoredValueProvider: StoredValueProvider {

    fun get(name: String, scope: String): Any?

    override fun get(name: String) = get(name, GLOBAL)

    companion object {
        const val GLOBAL = "global"
        const val CARD = "card"
    }
}
