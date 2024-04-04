package com.yandex.div.core.view2.reuse

import com.yandex.div.core.util.getDefaultState
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div

internal class NewToken(
    div: Div,
    override val parentToken: NewToken?,
    childIndex: Int,
    var lastExistingParent: ExistingToken?,
) : Token(div, parentToken, childIndex) {

    override fun getChildrenTokens(resolver: ExpressionResolver): List<NewToken> {
        return when (this.div) {
            is Div.Text -> listOf()
            is Div.Image -> listOf()
            is Div.GifImage -> listOf()
            is Div.Separator -> listOf()
            is Div.Indicator -> listOf()
            is Div.Slider -> listOf()
            is Div.Input -> listOf()
            is Div.Custom -> listOf()
            is Div.Select -> listOf()
            is Div.Video -> listOf()
            is Div.Container -> itemsToNewTokenList(this.div.value.buildItems(resolver))
            is Div.Grid -> itemsToNewTokenList(this.div.value.nonNullItems)
            is Div.Gallery -> itemsToNewTokenList(this.div.value.nonNullItems)
            is Div.Pager -> itemsToNewTokenList(this.div.value.nonNullItems)
            is Div.Tabs -> itemsToNewTokenList(this.div.value.items.map { it.div })
            is Div.State -> {
                val stateToBindDiv = this.div.value.getDefaultState(resolver)?.div ?: return listOf()
                itemsToNewTokenList(listOf(stateToBindDiv))
            }
        }
    }

    private fun itemsToNewTokenList(items: List<Div>): List<NewToken> {
        val tokens = mutableListOf<NewToken>()
        items.forEachIndexed { index, item ->
            val token = NewToken(
                div = item,
                childIndex = index,
                parentToken = this,
                lastExistingParent = this.lastExistingParent,
            )
            tokens.add(token)
        }
        return tokens
    }
}
