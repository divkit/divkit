package com.yandex.div.core.view2.reuse

import android.view.View
import android.view.ViewGroup
import com.yandex.div.core.view2.divs.gallery.DivGalleryAdapter
import com.yandex.div.core.view2.divs.pager.DivPagerAdapter
import com.yandex.div.core.view2.divs.widgets.DivCustomWrapper
import com.yandex.div.core.view2.divs.widgets.DivPagerView
import com.yandex.div.core.view2.divs.widgets.DivRecyclerView
import com.yandex.div.core.view2.divs.widgets.DivStateLayout
import com.yandex.div.internal.core.DivItemBuilderResult
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.itemsToDivItemBuilderResult
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.internal.core.toDivItemBuilderResult
import com.yandex.div.internal.core.toItemBuilderResult
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGrid
import com.yandex.div2.DivPager

internal class ExistingToken(
    item: DivItemBuilderResult,
    childIndex: Int,
    val view: View,
    val parentToken: ExistingToken?,
) : Token(item, childIndex) {

    fun getChildrenTokens(parentToken: ExistingToken? = null): List<ExistingToken> {
        return when (div) {
            is Div.Text -> emptyList()
            is Div.Image -> emptyList()
            is Div.GifImage -> emptyList()
            is Div.Separator -> emptyList()
            is Div.Indicator -> emptyList()
            is Div.Slider -> emptyList()
            is Div.Input -> emptyList()
            is Div.Select -> emptyList()
            is Div.Video -> emptyList()
            is Div.Switch -> emptyList()
            is Div.Container -> div.value.itemsToExistingTokenList(item.expressionResolver, parentToken)
            is Div.Custom -> div.value.itemsToExistingTokenList(item.expressionResolver, parentToken)
            is Div.Grid -> div.value.itemsToExistingTokenList(item.expressionResolver, parentToken)
            is Div.Gallery -> div.value.itemsToExistingTokenList(item.expressionResolver, parentToken)
            is Div.Pager -> div.value.itemsToExistingTokenList(item.expressionResolver, parentToken)
            is Div.Tabs -> throw RebindTask.UnsupportedElementException(div::class.java) // Not supported yet. Only full rebind
            is Div.State -> stateToExistingTokenList(item.expressionResolver, parentToken)
        }
    }

    private fun DivContainer.itemsToExistingTokenList(resolver: ExpressionResolver, parentToken: ExistingToken?): List<ExistingToken> {
        return simpleItemsToExistingTokenList(buildItems(resolver), parentToken)
    }

    private fun DivCustom.itemsToExistingTokenList(resolver: ExpressionResolver, parentToken: ExistingToken?): List<ExistingToken> {
        val tokens = mutableListOf<ExistingToken>()
        val customView = ((view as? DivCustomWrapper)?.customView as? ViewGroup) ?: return emptyList()
        nonNullItems.forEachIndexed { index, item ->
            val token = ExistingToken(
                item = item.toItemBuilderResult(resolver),
                view = customView.getChildAt(index) ?: return emptyList(),
                childIndex = index,
                parentToken = parentToken ?: this@ExistingToken,
            )
            tokens.add(token)
        }
        return tokens
    }

    private fun DivGrid.itemsToExistingTokenList(
        resolver: ExpressionResolver,
        parentToken: ExistingToken?
    ): List<ExistingToken> = simpleItemsToExistingTokenList(itemsToDivItemBuilderResult(resolver), parentToken)

    private fun stateToExistingTokenList(
        resolver: ExpressionResolver,
        parentToken: ExistingToken?
    ): List<ExistingToken> {
        val stateDiv = (view as? DivStateLayout)?.activeStateDiv ?: return emptyList()
        return simpleItemsToExistingTokenList(listOf(stateDiv).toDivItemBuilderResult(resolver), parentToken)
    }

    private fun DivPager.itemsToExistingTokenList(
        resolver: ExpressionResolver,
        parentToken: ExistingToken?
    ): List<ExistingToken> {
        val tokens = mutableListOf<ExistingToken>()
        val pager = (view as? DivPagerView)?.viewPager ?: return emptyList()
        val adapter = (pager.adapter as? DivPagerAdapter) ?: return emptyList()
        val activeHashes = adapter.itemsToShow.map { it.div.hash() }

        buildItems(resolver).forEachIndexed { index, newItem ->
            if (newItem.div.hash() in activeHashes) {
                val childViewIndex = activeHashes.indexOf(newItem.div.hash())
                val targetView = view.getPageView(childViewIndex) ?: return@forEachIndexed
                val token = ExistingToken(
                    item = newItem,
                    view = targetView,
                    childIndex = index,
                    parentToken = parentToken ?: this@ExistingToken,
                )
                tokens.add(token)
            }
        }

        return tokens
    }

    private fun DivGallery.itemsToExistingTokenList(
        resolver: ExpressionResolver,
        parentToken: ExistingToken?
    ): List<ExistingToken> {
        val tokens = mutableListOf<ExistingToken>()
        val adapter = ((view as? DivRecyclerView)?.adapter as? DivGalleryAdapter) ?: return emptyList()
        val activeHashes = adapter.visibleItems.map { it.div.hash() }

        buildItems(resolver).forEachIndexed { index, newItem ->
            if (newItem.div.hash() in activeHashes) {
                val targetView = view.getItemView(index) ?: return@forEachIndexed
                val token = ExistingToken(
                    item = newItem,
                    view = targetView,
                    childIndex = index,
                    parentToken = parentToken ?: this@ExistingToken,
                )
                tokens.add(token)
            }
        }

        return tokens
    }

    private fun simpleItemsToExistingTokenList(
        items: List<DivItemBuilderResult>,
        parentToken: ExistingToken?
    ): List<ExistingToken> {
        val tokens = mutableListOf<ExistingToken>()
        items.forEachIndexed { index, item ->
            val token = ExistingToken(
                item = item,
                view = (view as? ViewGroup)?.getChildAt(index) ?: return emptyList(),
                childIndex = index,
                parentToken = parentToken ?: this,
            )
            tokens.add(token)
        }
        return tokens
    }
}
