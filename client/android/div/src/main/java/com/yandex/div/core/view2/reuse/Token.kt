package com.yandex.div.core.view2.reuse

import com.yandex.div.internal.core.DivItemBuilderResult

internal abstract class Token(
    val item: DivItemBuilderResult,
    open val parentToken: Token?,
    val childIndex: Int,
) {
    val divHash: Int = item.div.propertiesHash()

    abstract fun getChildrenTokens(): List<Token>
}
