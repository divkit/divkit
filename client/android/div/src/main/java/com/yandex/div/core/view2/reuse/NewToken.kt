package com.yandex.div.core.view2.reuse

import com.yandex.div.core.util.getDefaultState
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.itemsToDivItemBuilderResult
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.internal.core.toDivItemBuilderResult
import com.yandex.div.internal.core.toItemBuilderResult
import com.yandex.div2.Div

internal class NewToken(
    item: DivItemBuilderResult,
    childIndex: Int,
    var lastExistingParent: ExistingToken?,
) : Token(item, childIndex) {

    fun getChildrenTokens(): List<NewToken> {
        val resolver = item.expressionResolver
        return when (val div = item.div) {
            is Div.Text -> listOf()
            is Div.Image -> listOf()
            is Div.GifImage -> listOf()
            is Div.Separator -> listOf()
            is Div.Indicator -> listOf()
            is Div.Slider -> listOf()
            is Div.Input -> listOf()
            is Div.Select -> listOf()
            is Div.Video -> listOf()
            is Div.Switch -> listOf()
            is Div.Container -> div.value.buildItems(divView = null, resolver = resolver).itemsToNewTokenList()
            is Div.Custom -> div.value.nonNullItems.toDivItemBuilderResult(resolver).itemsToNewTokenList()
            is Div.Grid -> div.value.itemsToDivItemBuilderResult(resolver).itemsToNewTokenList()
            is Div.Gallery -> div.value.buildItems(divView = null, resolver = resolver).itemsToNewTokenList()
            is Div.Pager -> div.value.buildItems(divView = null, resolver = resolver).itemsToNewTokenList()
            is Div.Tabs -> div.value.itemsToDivItemBuilderResult(resolver).itemsToNewTokenList()
            is Div.State -> {
                val stateToBindDiv = div.value.getDefaultState(resolver)?.div ?: return listOf()
                listOf(stateToBindDiv.toItemBuilderResult(resolver)).itemsToNewTokenList()
            }
        }
    }

    private fun List<DivItemBuilderResult>.itemsToNewTokenList(): List<NewToken> {
        val tokens = mutableListOf<NewToken>()
        forEachIndexed { index, item ->
            val token = NewToken(
                item = item,
                childIndex = index,
                lastExistingParent = this@NewToken.lastExistingParent,
            )
            tokens.add(token)
        }
        return tokens
    }
}
