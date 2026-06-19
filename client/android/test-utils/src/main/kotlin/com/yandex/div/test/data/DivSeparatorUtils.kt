package com.yandex.div.test.data

import com.yandex.div2.Div
import com.yandex.div2.DivExtension
import com.yandex.div2.DivSeparator

fun separator(
    extensions: List<DivExtension>? = null,
): Div = Div.Separator(
    DivSeparator(extensions = extensions)
)
