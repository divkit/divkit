package com.yandex.div.core.animation

import android.util.Property

internal abstract class FloatProperty<T>(name: String) : Property<T, Float>(Float::class.java, name) {

    /**
     * A type-specific variant of [set] that is faster when dealing
     * with fields of type `Float`.
     */
    abstract fun setValue(target: T, value: Float)

    override fun set(target: T, value: Float) {
        setValue(target, value)
    }
}
