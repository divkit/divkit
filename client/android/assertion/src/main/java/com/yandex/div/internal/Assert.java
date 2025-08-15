package com.yandex.div.internal;

import android.os.Looper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.yandex.div.assertion.BuildConfig;

/**
 * Contains methods to make assertions that help capture programming errors. Should be disabled in the production environment.
 * Disabled by default.
 */
public class Assert {

    @NonNull
    private static AssertionErrorHandler sAssertionErrorHandler = assertionError -> {
        throw assertionError;
    };

    private static volatile boolean sEnabled = false;

    private Assert() {
        // prevent instantiation
    }

    /**
     * Returns value indicating if assertions are enabled. This class won't throw any {@link AssertionError}, if disabled.
     */
    public static boolean isEnabled() {
        if (BuildConfig.DISABLE_ASSERTS) {
            return false;
        }

        return sEnabled;
    }

    public static void setEnabled(boolean enabled) {
        sEnabled = enabled;
    }

    /**
     * Asserts that a condition is true. If it isn't it throws an {@link AssertionError} with the given message.
     */
    public static void assertTrue(@Nullable String message, boolean condition) {
        if (!condition) {
            fail(message);
        }
    }

    /**
     * Asserts that a condition is true. If it isn't it throws an {@link AssertionError} without a message.
     */
    public static void assertTrue(boolean condition) {
        assertTrue(null, condition);
    }

    /**
     * Asserts that a condition is false. If it isn't it throws an {@link AssertionError} with the given message.
     */
    public static void assertFalse(@Nullable String message, boolean condition) {
        assertTrue(message, !condition);
    }

    /**
     * Asserts that a condition is false. If it isn't it throws an {@link AssertionError} without a message.
     */
    public static void assertFalse(boolean condition) {
        assertFalse(null, condition);
    }

    /**
     * Fails with no message.
     */
    public static void fail() {
        fail(null);
    }

    /**
     * Fails with the given message.
     */
    public static void fail(@Nullable String message) {
        if (BuildConfig.DISABLE_ASSERTS) {
            return;
        }

        if (sEnabled) {
            performFail(new AssertionError(message == null ? "" : message));
        }
    }

    /**
     * Fails with the given message and throwable that caused the failure..
     */
    public static void fail(@Nullable String message, @Nullable Throwable cause) {
        if (BuildConfig.DISABLE_ASSERTS) {
            return;
        }

        if (sEnabled) {
            AssertionError assertionError = new AssertionError(message);
            assertionError.initCause(cause);
            performFail(assertionError);
        }
    }

    /**
     * Set custom {@link AssertionErrorHandler} to override on fail behavior
     */
    public static void setAssertPerformer(@NonNull AssertionErrorHandler assertionErrorHandler) {
        sAssertionErrorHandler = assertionErrorHandler;
    }

    /**
     * Asserts that two objects are equal. If they are not, an {@link AssertionError} without a message is thrown.
     * If <code>expected</code> and <code>actual</code> are <code>null</code>, they are considered equal.
     *
     * @param expected expected value
     * @param actual   the value to check against <code>expected</code>
     */
    public static void assertEquals(@Nullable Object expected, @Nullable Object actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two objects are equal. If they are not, an {@link AssertionError} is thrown with the given message.
     * If <code>expected</code> and <code>actual</code> are <code>null</code>, they are considered equal.
     *
     * @param message  the identifying message for the {@link AssertionError} (<code>null</code> okay)
     * @param expected expected value
     * @param actual   actual value
     */
    public static void assertEquals(@Nullable String message, @Nullable Object expected, @Nullable Object actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        }
        if (expected instanceof String && actual instanceof String) {
            String cleanMessage = message == null ? "" : message;
            performFail(new ComparisonFailure(cleanMessage, (String) expected, (String) actual));
        } else {
            failNotEquals(message, expected, actual);
        }
    }

    /**
     * Asserts that two longs are equal. If they are not, an {@link AssertionError} is thrown.
     *
     * @param expected expected long value.
     * @param actual   actual long value
     */
    public static void assertEquals(long expected, long actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two ints are equal. If they are not, an {@link AssertionError} is thrown.
     *
     * @param expected expected long value.
     * @param actual   actual long value
     */
    public static void assertEquals(int expected, int actual) {
        assertEquals(null, expected, actual);
    }

    /**
     * Asserts that two longs are equal. If they are not, an {@link AssertionError} is thrown with the given message.
     *
     * @param message  the identifying message for the {@link AssertionError} (<code>null</code> okay)
     * @param expected long expected value.
     * @param actual   long actual value
     */
    public static void assertEquals(@Nullable String message, long expected, long actual) {
        assertEquals(message, (Long) expected, (Long) actual);
    }

    /**
     * Asserts that an object isn't null. If it is an {@link AssertionError} is thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError}
     * @param object  Object to check or <code>null</code>
     */
    public static void assertNotNull(@Nullable String message, @Nullable Object object) {
        assertTrue(message, object != null);
    }

    /**
     * Asserts that an object isn't null. If it is an {@link AssertionError} is thrown.
     *
     * @param object Object to check or <code>null</code>
     */
    public static void assertNotNull(@Nullable Object object) {
        assertNotNull(null, object);
    }

    /**
     * Asserts that an object is null. If it is not, an {@link AssertionError} is thrown with the given message.
     *
     * @param message the identifying message for the {@link AssertionError}
     * @param object  Object to check or <code>null</code>
     */
    public static void assertNull(@Nullable String message, @Nullable Object object) {
        assertTrue(message, object == null);
    }

    /**
     * Asserts that an object is null. If it isn't an {@link AssertionError} is thrown.
     *
     * @param object Object to check or <code>null</code>
     */
    public static void assertNull(@Nullable Object object) {
        assertNull(null, object);
    }

    /**
     * Asserts that two objects refer to the same object. If they are not, an {@link AssertionError} is thrown with the given message.
     *
     * @param message  the identifying message for the {@link AssertionError}
     * @param expected the expected object
     * @param actual   the object to compare to <code>expected</code>
     */
    public static void assertSame(@Nullable String message, @Nullable Object expected, @Nullable Object actual) {
        if (expected == actual) {
            return;
        }
        failNotSame(message, expected, actual);
    }

    /**
     * Asserts that two objects refer to the same object. If they are not the same, an {@link AssertionError} without a message is thrown.
     *
     * @param expected the expected object
     * @param actual   the object to compare to <code>expected</code>
     */
    public static void assertSame(@Nullable Object expected, @Nullable Object actual) {
        assertSame(null, expected, actual);
    }

    /**
     * Asserts that two objects do not refer to the same object. If they do refer to the same object,
     * an {@link AssertionError} is thrown with the given message.
     *
     * @param message    the identifying message for the {@link AssertionError}
     * @param unexpected the object you don't expect
     * @param actual     the object to compare to <code>unexpected</code>
     */
    public static void assertNotSame(@Nullable String message, @Nullable Object unexpected, @Nullable Object actual) {
        if (unexpected == actual) {
            failSame(message);
        }
    }

    /**
     * Asserts that two objects do not refer to the same object. If they do refer to the same object,
     * an {@link AssertionError} without a message is thrown.
     *
     * @param unexpected the object you don't expect
     * @param actual     the object to compare to <code>unexpected</code>
     */
    public static void assertNotSame(@Nullable Object unexpected, @Nullable Object actual) {
        assertNotSame(null, unexpected, actual);
    }

    public static void assertMainThread() {
        assertSame("Code run not in main thread!", Looper.getMainLooper(), Looper.myLooper());
    }

    public static void assertNotMainThread() {
        assertNotSame("Code run in main thread!", Looper.getMainLooper(), Looper.myLooper());
    }

    private static void failSame(@Nullable String message) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        fail(formatted + "expected not same");
    }

    private static void failNotSame(@Nullable String message, @Nullable Object expected, @Nullable Object actual) {
        String formatted = "";
        if (message != null) {
            formatted = message + " ";
        }
        fail(formatted + "expected same:<" + expected + "> was not:<" + actual + ">");
    }

    private static void failNotEquals(@Nullable String message, @Nullable Object expected, @Nullable Object actual) {
        fail(format(message, expected, actual));
    }

    static String format(@Nullable String message, @Nullable Object expected, @Nullable Object actual) {
        String formatted = "";
        if (message != null && !message.equals("")) {
            formatted = message + " ";
        }
        String expectedString = String.valueOf(expected);
        String actualString = String.valueOf(actual);
        if (expectedString.equals(actualString)) {
            return formatted + "expected: "
                    + formatClassAndValue(expected, expectedString)
                    + " but was: " + formatClassAndValue(actual, actualString);
        } else {
            return formatted + "expected:<" + expectedString + "> but was:<" + actualString + ">";
        }
    }

    private static String formatClassAndValue(@Nullable Object value, @Nullable String valueString) {
        String className = value == null ? "null" : value.getClass().getName();
        return className + "<" + valueString + ">";
    }

    private static void performFail(@NonNull AssertionError assertionError) {
        if (!isEnabled()) {
            return;
        }

        sAssertionErrorHandler.handleError(assertionError);
    }
}
