package com.yandex.div.data

interface Hashable {
    fun hash(): Int
    fun propertiesHash(): Int = hash()
}
