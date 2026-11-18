package com.yandex.div.evaluable.types

public expect class DateTime : Comparable<DateTime> {

    internal val timestampMillis: Long

    override fun toString(): String

    override fun equals(other: Any?): Boolean

    override fun hashCode(): Int

    override fun compareTo(other: DateTime): Int
}
