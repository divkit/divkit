package com.yandex.div.json

import org.json.JSONObject

interface JsonTemplate<T : JSONSerializable> {

    fun resolve(env: ParsingEnvironment, data: JSONObject): T
}
