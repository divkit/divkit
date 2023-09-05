package com.yandex.div.storage.rawjson

import org.json.JSONObject

interface RawJson {
    val id: String
    val data: JSONObject

    data class Ready(
        override val id: String,
        override val data: JSONObject,
    ) : RawJson

    companion object {
        operator fun invoke(id: String, data: JSONObject): RawJson {
            return Ready(id, data)
        }
    }
}
