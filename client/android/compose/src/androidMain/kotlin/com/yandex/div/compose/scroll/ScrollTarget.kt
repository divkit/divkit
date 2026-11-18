package com.yandex.div.compose.scroll

import com.yandex.div2.Div
import com.yandex.div2.DivActionScrollBy

internal class ScrollTarget(
    val items: List<Div>,
    val scrollTo: (index: Int, animated: Boolean, toEnd: Boolean, duration: Int?) -> Unit,
    val scrollToOffset: (offset: Int, animated: Boolean, duration: Int?) -> Unit,
    val scrollBy: (
        step: Int,
        offset: Int,
        overflow: DivActionScrollBy.Overflow,
        animated: Boolean,
        duration: Int?,
    ) -> Unit,
)
