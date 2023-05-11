package com.yandex.div.storage

import com.yandex.div.BuildConfig
import org.json.JSONObject
import java.io.File

fun createPayload(path: String): DivDataRepository.Payload {
    val json = JSONObject(File("${ BuildConfig.DIV2_JSON_PATH }/$path").readText(Charsets.UTF_8))
    return if (json.has("card")) {
        val template = json.optJSONObject("templates")?.toTemplatesMap() ?: emptyMap()
        val cardJson = json.getJSONObject("card")
        val rawDataAndMetadata = RawDataAndMetadata(path, cardJson)
        DivDataRepository.Payload(listOf(rawDataAndMetadata), template)
    } else {
        val rawDataAndMetadata = RawDataAndMetadata(path, json)
        DivDataRepository.Payload(listOf(rawDataAndMetadata), emptyMap())
    }
}

fun createPayload(paths: List<String>): DivDataRepository.Payload {
    val data = mutableListOf<RawDataAndMetadata>()
    val templates = mutableMapOf<String, JSONObject>()
    for (path in paths) {
        val json = JSONObject(File("${ BuildConfig.DIV2_JSON_PATH }/$path").readText(Charsets.UTF_8))
        if (json.has("card")) {
            val template = json.optJSONObject("templates")?.toTemplatesMap() ?: emptyMap()
            val cardJson = json.getJSONObject("card")
            data.add(RawDataAndMetadata(path, cardJson))
            templates.putAll(template)
        } else {
            data.add(RawDataAndMetadata(path, json))
        }
    }
    return DivDataRepository.Payload(data, templates)
}

private fun JSONObject.toTemplatesMap(): Map<String, JSONObject> {
    val map = mutableMapOf<String, JSONObject>()
    for (key in keys()) {
        map[key] = getJSONObject(key)
    }
    return map
}
