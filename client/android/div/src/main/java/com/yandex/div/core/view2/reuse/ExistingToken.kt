package com.yandex.div.core.view2.reuse

import android.view.View
import android.view.ViewGroup
import com.yandex.div.core.view2.divs.DivPatchableAdapter
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGrid
import com.yandex.div2.DivPager

internal class ExistingToken(
    item: DivItemBuilderResult,
    val view: View,
    override val parentToken: ExistingToken?,
    childIndex: Int,
) : Token(item, parentToken, childIndex) {

    override fun getChildrenTokens(): List<ExistingToken> {
        return when (val div = item.div) {
            is Div.Text -> emptyList()
            is Div.Image -> emptyList()
            is Div.GifImage -> emptyList()
            is Div.Separator -> emptyList()
            is Div.Indicator -> emptyList()
            is Div.Slider -> emptyList()
            is Div.Input -> emptyList()
            is Div.Custom -> emptyList()
            is Div.Select -> emptyList()
            is Div.Video -> emptyList()
            is Div.Container -> itemsToExistingTokenList(div.value, item.expressionResolver)
            is Div.Grid -> itemsToExistingTokenList(div.value, item.expressionResolver)
            is Div.Gallery -> itemsToExistingTokenList(div.value, item.expressionResolver)
            is Div.Pager -> itemsToExistingTokenList(div.value, item.expressionResolver)
            is Div.Tabs -> emptyList() // Not supported yet. Only full rebind
            is Div.State -> itemsToExistingTokenList(item.expressionResolver)
        }
    }

    private fun itemsToExistingTokenList(
        div: DivContainer,
        resolver: ExpressionResolver,
    ): List<ExistingToken> {
        return simpleItemsToExistingTokenList(div.buildItems(resolver))
    }

    private fun itemsToExistingTokenList(div: DivGrid, resolver: ExpressionResolver): List<ExistingToken> {
        return simpleItemsToExistingTokenList(div.nonNullItems.map { DivItemBuilderResult(it, resolver) })
    }

    private fun itemsToExistingTokenList(resolver: ExpressionResolver): List<ExistingToken> {
        val stateDiv = (view as? DivStateLayout)?.activeStateDiv ?: return emptyList()
        return simpleItemsToExistingTokenList(listOf(stateDiv).map { DivItemBuilderResult(it, resolver) })
    }

    private fun itemsToExistingTokenList(div: DivPager, resolver: ExpressionResolver): List<ExistingToken> {
        val tokens = mutableListOf<ExistingToken>()
        val pager = (view as? DivPagerView)?.viewPager ?: return emptyList()
        val adapter = (pager.adapter as? DivPatchableAdapter) ?: return emptyList()
        val activeItems = adapter.activeItems
        val activeHashes = activeItems.map { it.hash() }

        div.items?.forEachIndexed { index, newItem ->
            if (newItem.hash() in activeHashes) {
                val childViewIndex = activeHashes.indexOf(newItem.hash())
                val targetView = view.getPageView(childViewIndex) ?: return@forEachIndexed
                val token = ExistingToken(
                    item = DivItemBuilderResult(newItem, resolver),
                    view = targetView,
                    childIndex = index,
                    parentToken = this,
                )
                tokens.add(token)
            }
        }

        return tokens
    }

    private fun itemsToExistingTokenList(div: DivGallery, resolver: ExpressionResolver): List<ExistingToken> {
        val tokens = mutableListOf<ExistingToken>()
        val adapter = ((view as? DivRecyclerView)?.adapter as? DivPatchableAdapter) ?: return emptyList()
        val activeItems = adapter.activeItems
        val activeHashes = activeItems.map { it.hash() }

        div.items?.forEachIndexed { index, newItem ->
            if (newItem.hash() in activeHashes) {
                val targetView = view.getItemView(index) ?: return@forEachIndexed
                val token = ExistingToken(
                    item = DivItemBuilderResult(newItem, resolver),
                    view = targetView,
                    childIndex = index,
                    parentToken = this,
                )
                tokens.add(token)
            }
        }

        return tokens
    }

    private fun simpleItemsToExistingTokenList(items: List<DivItemBuilderResult>): List<ExistingToken> {
        val tokens = mutableListOf<ExistingToken>()
        items.forEachIndexed { index, item ->
            val token = ExistingToken(
                item = item,
                view = (view as? ViewGroup)?.getChildAt(index) ?: return emptyList(),
                childIndex = index,
                parentToken = this,
            )
            tokens.add(token)
        }
        return tokens
    }
}
