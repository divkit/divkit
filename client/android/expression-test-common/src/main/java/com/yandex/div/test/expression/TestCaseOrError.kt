package com.yandex.div.test.expression

class TestCaseOrError<T> private constructor(
    val testCase: T?,
    val error: TestCaseParsingError?,
) {
    fun getCaseOrThrow(): T {
        error?.throwError()
        return testCase!!
    }

    override fun toString(): String {
        return testCase?.toString() ?: error?.toString() ?: "???"
    }

    companion object {
        operator fun <T> invoke(testCase: T) = TestCaseOrError(testCase, null)
        operator fun <T> invoke(error: TestCaseParsingError) = TestCaseOrError<T>(null, error)
    }
}
