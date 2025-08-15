package com.yandex.div.core.widget

internal interface FixedLineHeightView {
    var fixedLineHeight: Int

    companion object {
        const val UNDEFINED_LINE_HEIGHT = -1
    }
}
