package com.yandex.div.internal

/**
 * Contains methods to make assertions that help capture programming errors. Should be disabled in the production environment.
 * Disabled by default.
 */
object Assert {

    private var _assertionErrorHandler = AssertionErrorHandler { throw it }

    @Volatile
    private var _isEnabled = false

    /**
     * Returns value indicating if assertions are enabled. This class won't throw any [AssertionError], if disabled.
     */
    @JvmStatic
    var isEnabled: Boolean
        get() {
            if (BuildKonfig.DISABLE_ASSERTS) {
                return false
            }
            return _isEnabled
        }
        set(value) {
            _isEnabled = value
        }

    /**
     * Asserts that a condition is true. If it isn't it throws an [AssertionError] with the given message.
     */
    @JvmStatic
    fun assertTrue(message: String?, condition: Boolean) {
        if (!condition) {
            fail(message)
        }
    }

    /**
     * Asserts that a condition is true. If it isn't it throws an [AssertionError] without a message.
     */
    @JvmStatic
    fun assertTrue(condition: Boolean) {
        assertTrue(null, condition)
    }

    /**
     * Asserts that a condition is false. If it isn't it throws an [AssertionError] without a message.
     */
    @JvmStatic
    fun assertFalse(condition: Boolean) {
        assertTrue(null, !condition)
    }

    /**
     * Fails with the given message.
     */
    @JvmOverloads
    @JvmStatic
    fun fail(message: String? = null) {
        if (isEnabled) {
            performFail(AssertionError(message ?: ""))
        }
    }

    /**
     * Fails with the given message and throwable that caused the failure.
     */
    @JvmStatic
    fun fail(message: String?, cause: Throwable?) {
        if (isEnabled) {
            val assertionError: java.lang.AssertionError = java.lang.AssertionError(message)
            assertionError.initCause(cause)
            performFail(assertionError)
        }
    }

    /**
     * Set custom [AssertionErrorHandler] to override on fail behavior
     */
    fun setAssertPerformer(assertionErrorHandler: AssertionErrorHandler) {
        _assertionErrorHandler = assertionErrorHandler
    }

    /**
     * Asserts that two objects are equal. If they are not, an [AssertionError] is thrown with the given message.
     * If `expected` and `actual` are `null`, they are considered equal.
     *
     * @param message  the identifying message for the [AssertionError] (`null` okay)
     * @param expected expected value
     * @param actual   actual value
     */
    fun assertEquals(message: String?, expected: Any?, actual: Any?) {
        if (expected == null && actual == null) {
            return
        }
        if (expected != null && expected == actual) {
            return
        }
        if (expected is String && actual is String) {
            performFail(ComparisonFailure(message ?: "", expected, actual))
        } else {
            failNotEquals(message, expected, actual)
        }
    }

    /**
     * Asserts that two ints are equal. If they are not, an [AssertionError] is thrown.
     *
     * @param expected expected long value.
     * @param actual   actual long value
     */
    fun assertEquals(expected: Int, actual: Int) {
        Assert.assertEquals(null, expected.toLong(), actual.toLong())
    }

    /**
     * Asserts that two longs are equal. If they are not, an [AssertionError] is thrown with the given message.
     *
     * @param message  the identifying message for the [AssertionError] (`null` okay)
     * @param expected long expected value.
     * @param actual   long actual value
     */
    fun assertEquals(message: String?, expected: Long, actual: Long) {
        assertEquals(message, expected as Long?, actual as Long?)
    }

    /**
     * Asserts that an object isn't null. If it is an [AssertionError] is thrown with the given message.
     *
     * @param message the identifying message for the [AssertionError]
     * @param obj  Object to check or `null`
     */
    @JvmStatic
    fun assertNotNull(message: String?, obj: Any?) {
        assertTrue(message, obj != null)
    }

    /**
     * Asserts that an object isn't null. If it is an [AssertionError] is thrown.
     *
     * @param obj Object to check or `null`
     */
    fun assertNotNull(obj: Any?) {
        assertNotNull(null, obj)
    }

    /**
     * Asserts that an object is null. If it isn't an [AssertionError] is thrown.
     *
     * @param obj Object to check or `null`
     */
    @JvmStatic
    fun assertNull(obj: Any?) {
        assertTrue(null, obj == null)
    }

    private fun failNotEquals(message: String?, expected: Any?, actual: Any?) {
        fail(format(message, expected, actual))
    }

    internal fun format(message: String?, expected: Any?, actual: Any?): String {
        var formatted = ""
        if (message != null && message != "") {
            formatted = "$message "
        }
        val expectedString = expected.toString()
        val actualString = actual.toString()
        return if (expectedString == actualString) {
            formatted + "expected: " + formatClassAndValue(expected, expectedString) +
                    " but was: " + formatClassAndValue(actual, actualString)
        } else {
            formatted + "expected:<" + expectedString + "> but was:<" + actualString + ">"
        }
    }

    private fun formatClassAndValue(value: Any?, valueString: String?): String {
        val className = if (value == null) "null" else value.javaClass.name
        return "$className<$valueString>"
    }

    private fun performFail(assertionError: java.lang.AssertionError) {
        if (!isEnabled) {
            return
        }
        _assertionErrorHandler.handleError(assertionError)
    }
}
