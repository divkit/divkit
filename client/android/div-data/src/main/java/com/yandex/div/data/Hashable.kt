package com.yandex.div.data

interface Hashable {
    @DivModelInternalApi
    fun hash(): Int

    @DivModelInternalApi
    fun propertiesHash(): Int = hash()
}
