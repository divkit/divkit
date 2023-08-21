package com.yandex.div.dsl

sealed class Property<out T>

class ReferenceProperty<T> internal constructor(val name: String) : Property<T>()

sealed class ValueProperty<T> : Property<T>()

class LiteralProperty<T> internal constructor(val value: T): ValueProperty<T>()

class ExpressionProperty<T> internal constructor(val expression: String): ValueProperty<T>()

fun <T> reference(name: String): ReferenceProperty<T> {
    return ReferenceProperty(name)
}

fun <T> value(value: T): LiteralProperty<T> {
    return LiteralProperty(value)
}

fun <T> optionalValue(value: T?): LiteralProperty<T>? {
    return if (value == null) null else LiteralProperty(value)
}

fun <T> expression(expression: String): ValueProperty<T> {
    return ExpressionProperty(expression)
}

fun int(intValue: Int) = value(intValue)

fun number(numberValue: Double) = value(numberValue)

fun bool(boolValue: Boolean) = value(boolValue)

fun string(stringValue: String) = value(stringValue)

fun <E : Enum<E>> enum(enumValue: E) = value(enumValue)
