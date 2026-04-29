package com.yandex.div.internal.parser

import org.json.JSONException
import org.json.JSONObject

class ParsingTestCase(
    val name: String,
    val templates: JSONObject?,
    val card: JSONObject,
    val expectedResult: ExpectedResult,
) {

    class ExpectedResult(
        val errorCount: Int?,
        val card: JSONObject?,
    )

    override fun toString() = name

    companion object {
        @Throws(JSONException::class)
        fun from(name: String, json: JSONObject): ParsingTestCase {
            val expectedResult = json.getJSONObject("expected")
            return ParsingTestCase(
                name = name,
                templates = json.optJSONObject("templates"),
                card = json.getJSONObject("card"),
                expectedResult = ExpectedResult(
                    errorCount = expectedResult.optInt("error_count"),
                    card = expectedResult.optJSONObject("card"),
                ),
            )
        }
    }
}
