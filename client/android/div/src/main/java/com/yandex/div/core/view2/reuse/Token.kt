package com.yandex.div.core.view2.reuse

import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div

internal abstract class Token(
    val div: Div,
    open val parentToken: Token?,
    val childIndex: Int,
) {
    val divHash: Int = div.propertiesHash()

    abstract fun getChildrenTokens(resolver: ExpressionResolver): List<Token>
}
