package com.yandex.div.model.entity

public sealed class Field<T>(
    @JvmField public val type: Int,
    @JvmField public val overridable: Boolean
) {

    public object Null : Field<Any>(type = TYPE_NULL, overridable = false)

    public object Placeholder : Field<Any>(type = TYPE_PLACEHOLDER, overridable = true)

    public class Value<T>(
        overridable: Boolean,
        @JvmField public val value: T
    ) : Field<T>(type = TYPE_VALUE, overridable)

    public class Reference<T> @JvmOverloads constructor(
        overridable: Boolean,
        @JvmField public val reference: String,
        @JvmField public val fallbackValue: T? = null
    ) : Field<T>(type = TYPE_REFERENCE, overridable)

    public companion object {

        public const val TYPE_NULL: Int = 0
        public const val TYPE_PLACEHOLDER: Int = 1
        public const val TYPE_VALUE: Int = 2
        public const val TYPE_REFERENCE: Int = 3

        @Suppress("UNCHECKED_CAST")
        public fun <T> nullField(overridable: Boolean): Field<T> {
            val nullField = if (overridable) Placeholder else Null
            return nullField as Field<T>
        }
    }
}

public fun <T> Field<T>?.clone(overridable: Boolean): Field<T> {
    return when {
        this == null || this == Field.Null || this == Field.Placeholder -> Field.nullField(overridable)
        this is Field.Value -> Field.Value(overridable, value)
        this is Field.Reference -> Field.Reference(overridable, reference, fallbackValue)
        else -> throw IllegalStateException("Unknown field type")
    }
}
