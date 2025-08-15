package com.yandex.div.rule

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.internal.util.asList
import com.yandex.divkit.demo.screenshot.DivAssetReader
import com.yandex.test.rules.SimpleStatement
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert
import org.junit.Assume
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class CheckCaseRule(private val casePath: String) : TestRule {
    private val context: Context = ApplicationProvider.getApplicationContext()

    override fun apply(base: Statement, description: Description): Statement = SimpleStatement {
        val json = DivAssetReader(context).read(casePath)

        json.checkField<String>(DESCRIPTION_FIELD)
        json.checkField<JSONArray>(PLATFORMS_FIELD)

        checkPlatformSupported(json)

        base.evaluate()
    }

    private fun checkPlatformSupported(json: JSONObject) {
        val platforms = json.getJSONArray(PLATFORMS_FIELD).asList<String>()
        Assume.assumeTrue(
            "Skipping test, '$VALUE_PLATFORM_ANDROID' not listed as target platform",
            platforms.contains(VALUE_PLATFORM_ANDROID)
        )
    }

    companion object {
        private inline fun <reified T> JSONObject.checkField(field: String) {
            Assert.assertTrue(
                "In test file '$field' field was not found",
                has(field)
            )
            Assert.assertTrue(
                "In test file '$DESCRIPTION_FIELD' is not ${T::class.simpleName}",
                get(field) is T
            )
        }

        private const val PLATFORMS_FIELD = "platforms"
        private const val DESCRIPTION_FIELD = "description"

        private const val VALUE_PLATFORM_ANDROID = "android"
    }
}
