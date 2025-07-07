package com.yandex.div.core.view2.divs

import com.yandex.div.BuildConfig
import com.yandex.div.data.DivParsingEnvironment
import com.yandex.div.json.ParsingErrorLogger
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import org.json.JSONObject
import java.io.File

class UnitTestData(
    private val dir: String,
    private val fileName: String,
) {

    val data: DivData
        get() = DivData(logId = "id", states = listOf(DivData.State(div, 0)))

    private var _div: Div? = null
    val div: Div
        get() {
            if (_div == null) {
                val path = "${BuildConfig.DIV2_JSON_PATH}/unit_test_data/$dir/$fileName"
                val jsonString = File(path).readText(Charsets.UTF_8)
                val json = JSONObject(jsonString)
                val environment = DivParsingEnvironment(ParsingErrorLogger.LOG)
                _div = Div(environment, json)
            }
            return _div ?: throw AssertionError("Unacceptable!")
        }

    val dataWithTemplates: DivData
        get() {
            val path = "${BuildConfig.DIV2_JSON_PATH}/unit_test_data/$dir/$fileName"
            val jsonString = File(path).readText(Charsets.UTF_8)
            val json = JSONObject(jsonString)
            val templates = json.getJSONObject("templates")
            val card = json.getJSONObject("card")

            val environment = DivParsingEnvironment(ParsingErrorLogger.LOG)
            environment.parseTemplates(templates)

            return DivData(environment, card)
        }

    val patchWithTemplates: DivPatch
        get() {
            val path = "${BuildConfig.DIV2_JSON_PATH}/unit_test_data/$dir/$fileName"
            val jsonString = File(path).readText(Charsets.UTF_8)
            val json = JSONObject(jsonString)
            val templates = json.getJSONObject("templates")
            val card = json.getJSONObject("patch")

            val environment = DivParsingEnvironment(ParsingErrorLogger.LOG)
            environment.parseTemplates(templates)

            return DivPatch(environment, card)
        }
}
