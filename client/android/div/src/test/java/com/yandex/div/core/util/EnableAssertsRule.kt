package com.yandex.div.core.util

import com.yandex.div.internal.Assert
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class EnableAssertsRule(val enable: Boolean = true) : TestRule {
    override fun apply(base: Statement?, description: Description?) = object : Statement() {
        override fun evaluate() {
            val savedValue = Assert.isEnabled()
            Assert.setEnabled(enable)

            try {
                base?.evaluate()
            } finally {
                Assert.setEnabled(savedValue)
            }
        }
    }
}
