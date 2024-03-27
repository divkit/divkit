package com.yandex.div.core.view2.reuse

import com.yandex.div.core.util.getDefaultState
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div2.Div

internal class NewToken(
    item: DivItemBuilderResult,
    override val parentToken: NewToken?,
    childIndex: Int,
    var lastExistingParent: ExistingToken?,
) : Token(item, parentToken, childIndex) {

    override fun getChildrenTokens(): List<NewToken> {
        return when (val div = item.div) {
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
            is Div.Container -> itemsToNewTokenList(div.value.buildItems(item.expressionResolver))
            is Div.Grid ->
                itemsToNewTokenList(div.value.nonNullItems.map { DivItemBuilderResult(it, item.expressionResolver) })
            is Div.Gallery ->
                itemsToNewTokenList(div.value.nonNullItems.map { DivItemBuilderResult(it, item.expressionResolver) })
            is Div.Pager ->
                itemsToNewTokenList(div.value.nonNullItems.map { DivItemBuilderResult(it, item.expressionResolver) })
            is Div.Tabs ->
                itemsToNewTokenList(div.value.items.map { DivItemBuilderResult(it.div, item.expressionResolver) })
            is Div.State -> {
                val stateToBindDiv = div.value.getDefaultState(item.expressionResolver)?.div ?: return listOf()
                itemsToNewTokenList(listOf(stateToBindDiv).map { DivItemBuilderResult(it, item.expressionResolver) })
            }
        }
    }

    private fun itemsToNewTokenList(items: List<DivItemBuilderResult>): List<NewToken> {
        val tokens = mutableListOf<NewToken>()
        items.forEachIndexed { index, item ->
            val token = NewToken(
                item = item,
                childIndex = index,
                parentToken = this,
                lastExistingParent = this.lastExistingParent,
            )
            tokens.add(token)
        }
        return tokens
    }
}
