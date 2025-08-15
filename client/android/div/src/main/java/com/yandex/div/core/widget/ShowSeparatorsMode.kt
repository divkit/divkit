package com.yandex.div.core.widget

import androidx.annotation.IntDef

@IntDef(flag = true, value = [
    ShowSeparatorsMode.NONE,
    ShowSeparatorsMode.SHOW_AT_START,
    ShowSeparatorsMode.SHOW_BETWEEN,
    ShowSeparatorsMode.SHOW_AT_END
])
internal annotation class ShowSeparatorsMode {
    companion object {
        const val NONE = 0
        const val SHOW_AT_START = 1
        const val SHOW_BETWEEN = 2
        const val SHOW_AT_END = 4
    }
}
