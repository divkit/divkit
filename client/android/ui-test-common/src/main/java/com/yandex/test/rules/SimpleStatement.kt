package com.yandex.test.rules

import org.junit.runners.model.Statement

/**
 * Used to improve code readability
 */
class SimpleStatement(private val func: () -> Unit) : Statement() {
    override fun evaluate() {
        func()
    }
}
