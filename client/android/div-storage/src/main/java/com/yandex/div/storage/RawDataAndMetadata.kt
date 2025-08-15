package com.yandex.div.storage

import org.json.JSONObject

interface RawDataAndMetadata {
    val id: String
    val divData: JSONObject
    val metadata: JSONObject?

    class Ready(
            override val id: String,
            override val divData: JSONObject,
            override val metadata: JSONObject?,
    ) : RawDataAndMetadata

    companion object {
        operator fun invoke(id: String,
                            divData: JSONObject,
                            metadata: JSONObject? = null,
        ): RawDataAndMetadata {
            return Ready(id, divData, metadata)
        }
    }
}
