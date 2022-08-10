// Copyright (c) 2022 Yandex LLC. All rights reserved.
// Author: Anton Gulevsky <gulevsky@yandex-team.ru>.

package com.yandex.div.dsl.serializer

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.yandex.div.dsl.type.Color

class ColorSerializer : StdSerializer<Color>(Color::class.java) {

    override fun serialize(value: Color, generator: JsonGenerator, provider: SerializerProvider) {
        generator.writeString(value.argbValue)
    }
}
