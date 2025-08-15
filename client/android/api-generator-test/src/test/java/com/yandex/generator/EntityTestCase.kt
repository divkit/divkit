package com.yandex.generator

import com.yandex.div.json.JSONSerializable
import com.yandex.div.json.ParsingEnvironment
import org.json.JSONObject

class EntityTestCase<T : JSONSerializable>(
    private val ctor: (ParsingEnvironment, JSONObject) -> T,
) {

    fun parse(env: ParsingEnvironment, json: String): T {
        return ctor.invoke(env, JSONObject(json))
    }
}
