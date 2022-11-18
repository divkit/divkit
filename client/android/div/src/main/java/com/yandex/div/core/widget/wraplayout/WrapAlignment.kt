package com.yandex.div.core.widget.wraplayout

import androidx.annotation.IntDef

@IntDef(
    WrapAlignment.START,
    WrapAlignment.END,
    WrapAlignment.CENTER,
    WrapAlignment.BASELINE
)
@Retention(AnnotationRetention.SOURCE)
annotation class WrapAlignment {
    companion object {
        const val START = 0
        const val END = 1
        const val CENTER = 2
        const val BASELINE = 3
    }
}
