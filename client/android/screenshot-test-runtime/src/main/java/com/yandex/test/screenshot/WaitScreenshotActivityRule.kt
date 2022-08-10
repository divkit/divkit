package com.yandex.test.screenshot

import com.yandex.test.idling.waitForCondition
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class WaitScreenshotActivityRule(
    private val caseKey: String,
) : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                base.evaluate()
                waitForCondition("all steps passed") {
                    ScreenshotTestState.isCompleted(caseKey)
                }
            }
        }
    }
}
