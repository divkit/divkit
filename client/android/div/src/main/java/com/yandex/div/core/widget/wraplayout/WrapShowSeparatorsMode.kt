package com.yandex.div.core.widget.wraplayout

import androidx.annotation.IntDef

@IntDef(flag = true, value = [
    WrapShowSeparatorsMode.NONE,
    WrapShowSeparatorsMode.SHOW_AT_START,
    WrapShowSeparatorsMode.SHOW_BETWEEN,
    WrapShowSeparatorsMode.SHOW_AT_END
])
annotation class WrapShowSeparatorsMode {
    companion object {
        const val NONE = 0
        const val SHOW_AT_START = 1
        const val SHOW_BETWEEN = 2
        const val SHOW_AT_END = 4
    }
}
