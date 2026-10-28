package com.yandex.div.test.data

import com.yandex.div.evaluable.types.Color
import com.yandex.div.json.ParsingErrorLogger
import org.json.JSONArray
import org.json.JSONObject

val throwingErrorLogger: ParsingErrorLogger = ParsingErrorLogger { throw it }

fun color(value: Long) = Color(value.toInt())

fun jsonObject(vararg entries: Pair<String, Any>): JSONObject = JSONObject(mapOf(*entries))

fun jsonArray(vararg elements: Any): JSONArray = JSONArray(elements.toList())
