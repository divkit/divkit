package com.yandex.div.core.view2.reuse

import com.yandex.div.core.util.getDefaultState
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.itemsToDivBlocks
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.internal.core.toBlock
import com.yandex.div.internal.core.toBlocks
import com.yandex.div2.Div

internal class NewToken(
    item: DivBlock,
    childIndex: Int,
    var lastExistingParent: ExistingToken?,
) : Token(item, childIndex) {

    fun getChildrenTokens(): List<NewToken> {
        val resolver = item.expressionResolver
        val path = item.path
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
            is Div.Container -> div.value.buildItems(resolver, path).itemsToNewTokenList()
            is Div.Custom -> div.value.nonNullItems.toBlocks(resolver, path).itemsToNewTokenList()
            is Div.Grid -> div.value.itemsToDivBlocks(resolver, path).itemsToNewTokenList()
            is Div.Gallery -> div.value.buildItems(resolver, path).itemsToNewTokenList()
            is Div.Pager -> div.value.buildItems(resolver, path).itemsToNewTokenList()
            is Div.Tabs -> div.value.itemsToDivBlocks(resolver, path).itemsToNewTokenList()
            is Div.State -> {
                val stateToBindDiv = div.value.getDefaultState(resolver)?.div ?: return listOf()
                listOf(stateToBindDiv.toBlock(resolver, path)).itemsToNewTokenList()
            }
        }
    }

    private fun List<DivBlock>.itemsToNewTokenList(): List<NewToken> {
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
