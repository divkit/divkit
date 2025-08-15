package com.yandex.div.core.view2.reuse

import com.yandex.div.core.util.type
import com.yandex.div.internal.core.DivItemBuilderResult

internal abstract class Token(
    val item: DivItemBuilderResult,
    val childIndex: Int,
) {
    val divHash: Int = item.div.propertiesHash()
    val div = item.div

    fun isCombinable(other: Token): Boolean {
        return divHash == other.divHash && div.type == other.div.type
    }
}
