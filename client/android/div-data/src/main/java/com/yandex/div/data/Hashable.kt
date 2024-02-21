package com.yandex.div.data

interface Hashable {
    fun propertiesHash(): Int = hash()

    fun hash(): Int
}
