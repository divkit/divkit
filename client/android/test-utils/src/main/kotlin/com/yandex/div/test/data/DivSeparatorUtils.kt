package com.yandex.div.test.data

import com.yandex.div2.Div
import com.yandex.div2.DivEdgeInsets
import com.yandex.div2.DivExtension
import com.yandex.div2.DivSeparator
import com.yandex.div2.DivSize

fun separator(
    extensions: List<DivExtension>? = null,
    height: DivSize = wrapContent(),
    id: String? = null,
    margins: DivEdgeInsets? = null,
    width: DivSize = matchParent(),
): Div = Div.Separator(
    DivSeparator(
        extensions = extensions,
        height = height,
        id = id,
        margins = margins,
        width = width,
    )
)
