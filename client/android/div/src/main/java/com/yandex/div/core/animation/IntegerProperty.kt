package com.yandex.div.core.animation

import android.util.Property

internal abstract class IntegerProperty<T>(name: String) : Property<T, Int>(Int::class.java, name) {

    /**
     * A type-specific variant of [set] that is faster when dealing
     * with fields of type `Int`.
     */
    abstract fun setValue(target: T, value: Int)

    override fun set(target: T, value: Int) {
        setValue(target, value)
    }
}
