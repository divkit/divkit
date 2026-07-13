package com.yandex.div.core.view2.items

/**
 * Direction of items navigation.
 */
internal enum class ScrollDirection(val value: Int) {
    NEXT(1),
    PREVIOUS(-1);

    companion object {
        fun from(step: Int) = if (step >= 0) NEXT else PREVIOUS
    }
}
