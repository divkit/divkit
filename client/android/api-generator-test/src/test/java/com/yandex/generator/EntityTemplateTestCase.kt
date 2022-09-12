package com.yandex.generator

import com.yandex.div.json.JSONSerializable
import com.yandex.div.json.ParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import org.json.JSONObject
import java.io.File

class EntityTemplateTestCase<T : JSONSerializable>(
    private val ctor: (ParsingEnvironment, JSONObject) -> T,
) {

    private val environment = EntityParsingEnvironment(ParsingErrorLogger.LOG)

    fun parse(directory: String, filename: String): T {
        val bytes = readData(directory, filename)
        return parse(jsonObject(bytes))
    }

    private fun readData(directory: String, filename: String): ByteArray {
        val path = "${BuildConfig.TEMPLATES_JSON_PATH}/template_test_data/$directory/$filename"
        return File(path).readBytes()
    }

    private fun parse(json: JSONObject): T {
        val templatesJson = json.optJSONObject("templates")
        if (templatesJson != null) {
            environment.parseTemplates(templatesJson)
        }
        val entityJson = json.getJSONObject("entity")
        return ctor.invoke(environment, entityJson)
    }

    private fun jsonObject(json: ByteArray): JSONObject {
        return JSONObject(json.toString(charset = Charsets.UTF_8))
    }
}
