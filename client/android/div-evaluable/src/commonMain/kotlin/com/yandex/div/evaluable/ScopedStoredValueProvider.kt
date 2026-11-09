package com.yandex.div.evaluable

public interface ScopedStoredValueProvider: StoredValueProvider {

    public fun get(name: String, scope: String): Any?

    override fun get(name: String): Any? = get(name, GLOBAL)

    public companion object {
        public const val GLOBAL: String = "global"
        public const val CARD: String = "card"
    }
}
