package com.yandex.div.internal

/**
 * Kotlin wrapper for [Assert] using inline [Assert.isEnabled] checks.
 */
public object KAssert {

    /**
     * @see [Assert.fail]
     */
    public inline fun fail(message: () -> String) {
        if (Assert.isEnabled) {
            Assert.fail(message())
        }
    }

    /**
     * @see [Assert.fail]
     */
    public inline fun fail(cause: Throwable?, message: () -> String = { "" }) {
        if (Assert.isEnabled) {
            Assert.fail(message(), cause)
        }
    }

    /**
     * @see [Assert.assertEquals]
     */
    public inline fun assertEquals(expected: Any?, actual: Any?, message: () -> String = { "" }) {
        if (Assert.isEnabled) {
            Assert.assertEquals(message(), expected, actual)
        }
    }
}
