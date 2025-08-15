package com.yandex.div.core.widget.wraplayout

import androidx.annotation.IntDef

@IntDef(WrapDirection.ROW, WrapDirection.COLUMN)
@Retention(AnnotationRetention.SOURCE)
internal annotation class WrapDirection {
    companion object {
        const val ROW = 0
        const val COLUMN = 1
    }
}
