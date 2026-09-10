package com.yandex.div.test.data

import com.yandex.div.evaluable.types.Color
import com.yandex.div.json.ParsingErrorLogger

val throwingErrorLogger: ParsingErrorLogger = ParsingErrorLogger { throw it }

fun color(value: Long) = Color(value.toInt())
