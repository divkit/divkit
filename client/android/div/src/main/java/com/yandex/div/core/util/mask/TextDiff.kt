package com.yandex.div.core.util.mask

internal data class TextDiff(
    val start: Int,
    val added: Int,
    val removed: Int
) {
    companion object {
        fun build(left: String, right: String): TextDiff {
            if (left.length > right.length) {
                val diff = build(right, left)

                return TextDiff(
                    diff.start,
                    diff.removed,
                    diff.added
                )
            }

            var leftIndex = 0
            var rightIndex = right.length - 1
            val lengthDiff = right.length - left.length

            while (leftIndex < rightIndex && leftIndex < left.length && left[leftIndex] == right[leftIndex]) {
                leftIndex++
            }

            while (rightIndex - lengthDiff >= leftIndex && left[rightIndex - lengthDiff] == right[rightIndex]) {
                rightIndex--
            }

            rightIndex++

            return TextDiff(
                start = leftIndex,
                removed = rightIndex - leftIndex - lengthDiff,
                added = rightIndex - leftIndex
            )
        }
    }
}
