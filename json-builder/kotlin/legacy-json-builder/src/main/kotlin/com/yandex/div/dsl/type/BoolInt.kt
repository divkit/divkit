package com.yandex.div.dsl.type

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.yandex.div.dsl.serializer.BoolIntSerializer

@JsonSerialize(using = BoolIntSerializer::class)
enum class BoolInt(val intValue: Int) {
    FALSE(0),
    TRUE(1)
}
