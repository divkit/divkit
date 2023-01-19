package com.yandex.div.rule

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.yandex.div.internal.util.asList
import com.yandex.divkit.demo.screenshot.DivAssetReader
import com.yandex.test.rules.SimpleStatement
import org.json.JSONObject
import org.junit.Assume
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class CheckCaseRule(private val casePath: String) : TestRule {
    private val context: Context = ApplicationProvider.getApplicationContext()

    override fun apply(base: Statement, description: Description): Statement = SimpleStatement {
        val json = DivAssetReader(context).read(casePath)
        val platforms = parsePlatforms(json)

        Assume.assumeTrue("Skipping test, 'android' not listed as target platform", isForAndroidPlatform(platforms))

        base.evaluate()
    }

    private fun parsePlatforms(json: JSONObject): List<String> {
        return json.getJSONArray(PLATFORMS_FIELD).asList()
    }

    private fun isForAndroidPlatform(platforms: List<String>): Boolean {
        return platforms.contains(VALUE_PLATFORM_ANDROID)
    }

    companion object {
        private const val PLATFORMS_FIELD = "platforms"
        private const val VALUE_PLATFORM_ANDROID = "android"
    }
}
