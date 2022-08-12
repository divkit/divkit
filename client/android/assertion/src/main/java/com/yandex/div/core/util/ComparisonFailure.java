package com.yandex.div.core.util;

/**
 * Thrown when an {@link Assert#assertEquals(Object, Object)} fails.
 */
public class ComparisonFailure extends AssertionError {
    /**
     * The maximum length for mExpected and mActual. If it is exceeded, the strings should be shortened.
     *
     * @see ComparisonCompactor
     */
    private static final int MAX_CONTEXT_LENGTH = 20;
    private static final long serialVersionUID = 1L;

    private String mExpected;
    private String mActual;

    /**
     * Constructs a comparison failure.
     *
     * @param message the identifying message or null
     * @param expected the expected string value
     * @param actual the actual string value
     */
    public ComparisonFailure(String message, String expected, String actual) {
        super(message);
        mExpected = expected;
        mActual = actual;
    }

    /**
     * Returns "..." in place of common prefix and "..." in
     * place of common suffix between expected and actual.
     *
     * @see Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        return new ComparisonCompactor(MAX_CONTEXT_LENGTH, mExpected, mActual).compact(super.getMessage());
    }

    /**
     * Returns the actual string value
     *
     * @return the actual string value
     */
    public String getActual() {
        return mActual;
    }

    /**
     * Returns the expected string value
     *
     * @return the expected string value
     */
    public String getExpected() {
        return mExpected;
    }

    private static class ComparisonCompactor {
        private static final String ELLIPSIS = "...";
        private static final String DELTA_END = "]";
        private static final String DELTA_START = "[";

        /**
         * The maximum length for <code>expected</code> and <code>actual</code>. When <code>contextLength</code>
         * is exceeded, the Strings are shortened
         */
        private int mContextLength;
        private String mExpected;
        private String mActual;
        private int mPrefix;
        private int mSuffix;

        /**
         * @param contextLength the maximum length for <code>expected</code> and <code>actual</code>. When contextLength
         * is exceeded, the Strings are shortened
         * @param expected the expected string value
         * @param actual the actual string value
         */
        public ComparisonCompactor(int contextLength, String expected, String actual) {
            mContextLength = contextLength;
            mExpected = expected;
            mActual = actual;
        }

        private String compact(String message) {
            if (mExpected == null || mActual == null || areStringsEqual()) {
                return Assert.format(message, mExpected, mActual);
            }

            findCommonPrefix();
            findCommonSuffix();
            String expected = compactString(mExpected);
            String actual = compactString(mActual);
            return Assert.format(message, expected, actual);
        }

        private String compactString(String source) {
            String result = DELTA_START + source.substring(mPrefix, source.length() - mSuffix + 1) + DELTA_END;
            if (mPrefix > 0) {
                result = computeCommonPrefix() + result;
            }
            if (mSuffix > 0) {
                result = result + computeCommonSuffix();
            }
            return result;
        }

        private void findCommonPrefix() {
            mPrefix = 0;
            int end = Math.min(mExpected.length(), mActual.length());
            for (; mPrefix < end; mPrefix++) {
                if (mExpected.charAt(mPrefix) != mActual.charAt(mPrefix)) {
                    break;
                }
            }
        }

        private void findCommonSuffix() {
            int expectedSuffix = mExpected.length() - 1;
            int actualSuffix = mActual.length() - 1;
            for (; actualSuffix >= mPrefix && expectedSuffix >= mPrefix; actualSuffix--, expectedSuffix--) {
                if (mExpected.charAt(expectedSuffix) != mActual.charAt(actualSuffix)) {
                    break;
                }
            }
            mSuffix = mExpected.length() - expectedSuffix;
        }

        private String computeCommonPrefix() {
            return (mPrefix > mContextLength ? ELLIPSIS : "") + mExpected.substring(Math.max(0, mPrefix - mContextLength), mPrefix);
        }

        private String computeCommonSuffix() {
            int end = Math.min(mExpected.length() - mSuffix + 1 + mContextLength, mExpected.length());
            return mExpected.substring(mExpected.length() - mSuffix + 1, end) + (mExpected.length() - mSuffix + 1 < mExpected.length() -
                    mContextLength ? ELLIPSIS : "");
        }

        private boolean areStringsEqual() {
            return mExpected.equals(mActual);
        }
    }
}
