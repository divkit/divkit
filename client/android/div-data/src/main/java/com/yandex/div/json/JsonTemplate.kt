package com.yandex.div.json

import com.yandex.div.data.DivModelInternalApi
import com.yandex.div.data.EntityTemplate
import org.json.JSONObject

interface JsonTemplate<T : JSONSerializable> : EntityTemplate<T> {

    @DivModelInternalApi
    fun resolve(env: ParsingEnvironment, data: JSONObject): T
}
