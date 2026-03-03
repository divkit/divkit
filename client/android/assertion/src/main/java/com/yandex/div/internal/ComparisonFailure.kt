package com.yandex.div.internal

import kotlin.math.max
import kotlin.math.min

/**
 * Thrown when an [Assert.assertEquals] fails.
 */
internal class ComparisonFailure(
    message: String?,
    val expected: String,
    val actual: String
) : AssertionError(message) {

    /**
     * Returns "..." in place of common prefix and "..." in
     * place of common suffix between expected and actual.
     *
     * @see Throwable.getMessage
     */
    override val message: String
        get() = ComparisonCompactor(MAX_CONTEXT_LENGTH, expected, actual).compact(super.message)

    private class ComparisonCompactor(
        private val contextLength: Int,
        private val expected: String?,
        private val actual: String?
    ) {

        private var prefix = 0
        private var suffix = 0

        fun compact(message: String?): String {
            if (expected == null || actual == null || areStringsEqual()) {
                return Assert.format(message, expected, actual)
            }
            findCommonPrefix()
            findCommonSuffix()
            val expected = compactString(expected)
            val actual = compactString(actual)
            return Assert.format(message, expected, actual)
        }

        private fun compactString(source: String): String {
            var result = DELTA_START + source.substring(prefix, source.length - suffix + 1) + DELTA_END
            if (prefix > 0) {
                result = computeCommonPrefix() + result
            }
            if (suffix > 0) {
                result += computeCommonSuffix()
            }
            return result
        }

        private fun findCommonPrefix() {
            prefix = 0
            val end = min(expected!!.length, actual!!.length)
            while (prefix < end) {
                if (expected[prefix] != actual[prefix]) {
                    break
                }
                prefix++
            }
        }

        private fun findCommonSuffix() {
            var expectedSuffix = expected!!.length - 1
            var actualSuffix = actual!!.length - 1
            while (actualSuffix >= prefix && expectedSuffix >= prefix) {
                if (expected[expectedSuffix] != actual[actualSuffix]) {
                    break
                }
                actualSuffix--
                expectedSuffix--
            }
            suffix = expected.length - expectedSuffix
        }

        private fun computeCommonPrefix(): String {
            val prefixString = if (prefix > contextLength) ELLIPSIS else ""
            return prefixString + expected!!.substring(max(0, prefix - contextLength), prefix)
        }

        private fun computeCommonSuffix(): String {
            val end = min(expected!!.length - suffix + 1 + contextLength, expected.length)
            val suffixString = if (expected.length - suffix + 1 < expected.length - contextLength) ELLIPSIS else ""
            return expected.substring(expected.length - suffix + 1, end) + suffixString
        }

        private fun areStringsEqual(): Boolean {
            return expected == actual
        }

        companion object {
            private const val ELLIPSIS = "..."
            private const val DELTA_END = "]"
            private const val DELTA_START = "["
        }
    }

    private companion object {
        /**
         * The maximum length for mExpected and mActual. If it is exceeded, the strings should be shortened.
         *
         * @see ComparisonCompactor
         */
        private const val MAX_CONTEXT_LENGTH = 20
        private const val serialVersionUID = 1L
    }
}
