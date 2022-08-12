@file:Suppress("NOTHING_TO_INLINE")

package com.yandex.div.core.util

/**
 * Kotlin wrapper for [Assert] using inline [Assert.isEnabled] checks.
 */
object KAssert {

    /**
     * @see [Assert.fail]
     */
    inline fun fail(message: () -> String) {
        if (Assert.isEnabled()) {
            Assert.fail(message())
        }
    }

    /**
     * @see [Assert.fail]
     */
    inline fun fail(cause: Throwable?, message: () -> String = { "" }) {
        if (Assert.isEnabled()) {
            Assert.fail(message(), cause)
        }
    }

    /**
     * [KAssert.fail] + [KLog.e]
     */
    inline fun failWithLog(tag: String, message: () -> String) {
        KLog.e(tag, message)
        fail(message)
    }

    /**
     * [KAssert.fail] + [KLog.e]
     */
    inline fun failWithLog(tag: String, cause: Throwable, message: () -> String = { "" }) {
        KLog.e(tag, cause, message)
        fail(cause, message)
    }

    /**
     * @see [Assert.assertTrue]
     */
    inline fun assertTrue(condition: Boolean, message: () -> String = { "" }) {
        if (Assert.isEnabled() && !condition) {
            Assert.fail(message())
        }
    }

    /**
     * @see [Assert.assertFalse]
     */
    inline fun assertFalse(condition: Boolean, message: () -> String = { "" }) {
        if (Assert.isEnabled() && condition) {
            Assert.fail(message())
        }
    }

    /**
     * @see [Assert.assertTrue]
     */
    inline fun assertTrue(condition: () -> Boolean, message: () -> String = { "" }) {
        if (Assert.isEnabled() && !condition()) {
            Assert.fail(message())
        }
    }

    /**
     * @see [Assert.assertFalse]
     */
    inline fun assertFalse(condition: () -> Boolean, message: () -> String = { "" }) {
        if (Assert.isEnabled() && condition()) {
            Assert.fail(message())
        }
    }

    /**
     * @see [Assert.assertEquals]
     */
    inline fun assertEquals(expected: Any?, actual: Any?, message: () -> String = { "" }) {
        if (Assert.isEnabled()) {
            Assert.assertEquals(message(), expected, actual)
        }
    }

    /**
     * @see [Assert.assertNotNull]
     */
    inline fun assertNotNull(nullable: Any?, message: () -> String = { "" }) {
        if (Assert.isEnabled() && nullable == null) {
            Assert.fail(message())
        }
    }

    /**
     * @see [Assert.assertNull]
     */
    inline fun assertNull(nullable: Any?, message: () -> String = { "" }) {
        if (Assert.isEnabled() && nullable != null) {
            Assert.fail(message())
        }
    }

    /**
     * @see [Assert.assertSame]
     */
    inline fun assertSame(expected: Any?, actual: Any?, message: () -> String = { "" }) {
        if (Assert.isEnabled()) {
            Assert.assertSame(message(), expected, actual)
        }
    }

    /**
     * @see [Assert.assertNotSame]
     */
    inline fun assertNotSame(expected: Any?, actual: Any?, message: () -> String = { "" }) {
        if (Assert.isEnabled()) {
            Assert.assertNotSame(message(), expected, actual)
        }
    }

    /**
     * @see [Assert.assertMainThread]
     */
    inline fun assertMainThread() {
        if (Assert.isEnabled()) {
            Assert.assertMainThread()
        }
    }

    /**
     * @see [Assert.assertNotMainThread]
     */
    inline fun assertNotMainThread() {
        if (Assert.isEnabled()) {
            Assert.assertNotMainThread()
        }
    }
}
