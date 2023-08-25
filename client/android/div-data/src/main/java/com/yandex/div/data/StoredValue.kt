package com.yandex.div.data

import android.net.Uri
import com.yandex.div.evaluable.types.Color

sealed class StoredValue {
    abstract val name: String
    abstract val lifetimeInSeconds: Long

    data class StringStoredValue(
        override val name: String,
        override val lifetimeInSeconds: Long,
        val value: String,
    ) : StoredValue()

    data class IntegerStoredValue(
        override val name: String,
        override val lifetimeInSeconds: Long,
        val value: Long,
    ) : StoredValue()

    data class BooleanStoredValue(
        override val name: String,
        override val lifetimeInSeconds: Long,
        val value: Boolean,
    ) : StoredValue()

    data class DoubleStoredValue(
        override val name: String,
        override val lifetimeInSeconds: Long,
        val value: Double,
    ) : StoredValue()

    data class ColorStoredValue(
        override val name: String,
        override val lifetimeInSeconds: Long,
        val value: Color,
    ) : StoredValue()

    data class UrlStoredValue(
        override val name: String,
        override val lifetimeInSeconds: Long,
        val value: Uri,
    ) : StoredValue()

    fun getValue(): Any = when (this) {
        is StringStoredValue -> value
        is IntegerStoredValue -> value
        is BooleanStoredValue -> value
        is DoubleStoredValue -> value
        is ColorStoredValue -> value
        is UrlStoredValue -> value
    }

}
