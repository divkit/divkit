package com.yandex.div.utils

/**
 * [CharSequence] utility methods.
 *
 * see https://cs.android.com/android/platform/superproject/+/master:frameworks/base/core/java/com/android/internal/util/CharSequences.java;drc=ae5bcf23b5f0875e455790d6af387184dbd009c1;l=99
 */
object CharSequences {

    /**
     * Compares two character sequences for equality.
     */
    fun equals(a: CharSequence, b: CharSequence): Boolean {
        if (a.length != b.length) {
            return false
        }
        val length = a.length
        for (i in 0 until length) {
            if (a[i] != b[i]) {
                return false
            }
        }
        return true
    }
}
