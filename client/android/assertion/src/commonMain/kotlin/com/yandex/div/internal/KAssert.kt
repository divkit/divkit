package com.yandex.div.internal

/**
 * Kotlin wrapper for [Assert] using inline [Assert.isEnabled] checks.
 */
object KAssert {

    /**
     * @see [Assert.fail]
     */
    inline fun fail(message: () -> String) {
        if (Assert.isEnabled) {
            Assert.fail(message())
        }
    }

    /**
     * @see [Assert.fail]
     */
    inline fun fail(cause: Throwable?, message: () -> String = { "" }) {
        if (Assert.isEnabled) {
            Assert.fail(message(), cause)
        }
    }

    /**
     * @see [Assert.assertEquals]
     */
    inline fun assertEquals(expected: Any?, actual: Any?, message: () -> String = { "" }) {
        if (Assert.isEnabled) {
            Assert.assertEquals(message(), expected, actual)
        }
    }
}
