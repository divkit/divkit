package com.yandex.div.dsl.context

import com.yandex.div.dsl.Property
import com.yandex.div.dsl.ReferenceProperty
import com.yandex.div.dsl.ValueProperty
import com.yandex.div.dsl.value

sealed class TemplateBinding<T>

class PropertyOverriding<T> internal constructor(
    val name: String,
    val property: Property<T>
) : TemplateBinding<T>()

fun <T> TemplateContext<*>.override(name: String, property: Property<T>): PropertyOverriding<T> {
    return PropertyOverriding(name, property)
}

fun <T> TemplateContext<*>.override(name: String, value: T): PropertyOverriding<T> {
    return PropertyOverriding(name, value(value))
}

fun <T> CardContext.override(name: String, property: ValueProperty<T>): PropertyOverriding<T> {
    return PropertyOverriding(name, property)
}

fun <T> CardContext.override(name: String, value: T): PropertyOverriding<T> {
    return PropertyOverriding(name, value(value))
}

class ReferenceResolving<T> internal constructor(
    val reference: ReferenceProperty<T>,
    val property: ValueProperty<T>
) : TemplateBinding<T>()

fun <T> CardContext.resolve(reference: ReferenceProperty<T>, property: ValueProperty<T>): ReferenceResolving<T> {
    return ReferenceResolving(reference, property)
}

fun <T> CardContext.resolve(reference: ReferenceProperty<T>, value: T): ReferenceResolving<T> {
    return ReferenceResolving(reference, value(value))
}
