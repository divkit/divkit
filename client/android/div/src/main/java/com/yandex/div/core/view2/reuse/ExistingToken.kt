package com.yandex.div.core.view2.reuse

import android.view.View
import android.view.ViewGroup
import com.yandex.div.core.view2.divs.DivPatchableAdapter
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGrid
import com.yandex.div2.DivPager

internal class ExistingToken(
    div: Div,
    val view: View,
    override val parentToken: ExistingToken?,
    childIndex: Int,
) : Token(div, parentToken, childIndex) {

    override fun getChildrenTokens(resolver: ExpressionResolver): List<ExistingToken> {
        return when (this.div) {
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
            is Div.Container -> itemsToExistingTokenList(this.div.value, resolver)
            is Div.Grid -> itemsToExistingTokenList(this.div.value)
            is Div.Gallery -> itemsToExistingTokenList(this.div.value)
            is Div.Pager -> itemsToExistingTokenList(this.div.value)
            is Div.Tabs -> emptyList() // Not supported yet. Only full rebind
            is Div.State -> itemsToExistingTokenList()
        }
    }

    private fun itemsToExistingTokenList(
        div: DivContainer,
        resolver: ExpressionResolver,
    ): List<ExistingToken> {
        return simpleItemsToExistingTokenList(div.buildItems(resolver))
    }

    private fun itemsToExistingTokenList(div: DivGrid): List<ExistingToken> {
        return simpleItemsToExistingTokenList(div.nonNullItems)
    }

    private fun itemsToExistingTokenList(): List<ExistingToken> {
        val stateDiv = (view as? DivStateLayout)?.activeStateDiv ?: return emptyList()
        return simpleItemsToExistingTokenList(listOf(stateDiv))
    }

    private fun itemsToExistingTokenList(div: DivPager): List<ExistingToken> {
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
                    div = newItem,
                    view = targetView,
                    childIndex = index,
                    parentToken = this,
                )
                tokens.add(token)
            }
        }

        return tokens
    }

    private fun itemsToExistingTokenList(div: DivGallery): List<ExistingToken> {
        val tokens = mutableListOf<ExistingToken>()
        val adapter = ((view as? DivRecyclerView)?.adapter as? DivPatchableAdapter) ?: return emptyList()
        val activeItems = adapter.activeItems
        val activeHashes = activeItems.map { it.hash() }

        div.items?.forEachIndexed { index, newItem ->
            if (newItem.hash() in activeHashes) {
                val targetView = view.getItemView(index) ?: return@forEachIndexed
                val token = ExistingToken(
                    div = newItem,
                    view = targetView,
                    childIndex = index,
                    parentToken = this,
                )
                tokens.add(token)
            }
        }

        return tokens
    }

    private fun simpleItemsToExistingTokenList(items: List<Div>): List<ExistingToken> {
        val tokens = mutableListOf<ExistingToken>()
        items.forEachIndexed { index, item ->
            val token = ExistingToken(
                div = item,
                view = (view as? ViewGroup)?.getChildAt(index) ?: return emptyList(),
                childIndex = index,
                parentToken = this,
            )
            tokens.add(token)
        }
        return tokens
    }
}
