package com.yandex.div.rule

import android.os.Build
import io.qameta.allure.kotlin.Allure
import org.junit.rules.ExternalResource

class AllureMetadataRule(private val casePath: String, private val caseRoot: String) : ExternalResource() {
    override fun before() {
        Allure.parameter("Case", casePath.removePrefix("$caseRoot/"))
        Allure.label("androidApi", Build.VERSION.SDK_INT.toString())
    }
}
