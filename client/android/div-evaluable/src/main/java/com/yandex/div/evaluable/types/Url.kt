package com.yandex.div.evaluable.types

@JvmInline
value class Url(val value: String) {
    override fun toString() = value
}
