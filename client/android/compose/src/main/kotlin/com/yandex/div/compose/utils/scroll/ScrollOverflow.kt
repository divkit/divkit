package com.yandex.div.compose.utils.scroll

import com.yandex.div.render.common.scroll.DivScrollActionUtils
import com.yandex.div2.DivActionScrollBy

internal fun DivActionScrollBy.Overflow.toScrollOverflow(): DivScrollActionUtils.Overflow {
    return when (this) {
        DivActionScrollBy.Overflow.CLAMP -> DivScrollActionUtils.Overflow.CLAMP
        DivActionScrollBy.Overflow.RING -> DivScrollActionUtils.Overflow.RING
    }
}
