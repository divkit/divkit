package com.yandex.div.core.player

import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.core.buildItems
import com.yandex.div.internal.core.nonNullItems
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivBase
import com.yandex.div2.DivContainer
import com.yandex.div2.DivCustom
import com.yandex.div2.DivData
import com.yandex.div2.DivGallery
import com.yandex.div2.DivGrid
import com.yandex.div2.DivPager
import com.yandex.div2.DivState
import com.yandex.div2.DivTabs
import com.yandex.div2.DivVideo
import javax.inject.Inject

@DivScope
internal class DivVideoActionHandler @Inject constructor(
    private val videoViewMapper: DivVideoViewMapper
) {
    fun handleAction(div2View: Div2View, divId: String, action: String): Boolean {
        val divData = div2View.divData ?: return false
        val video = searchDivDataForVideo(divData, divId, div2View.expressionResolver) ?: return false
        val player = videoViewMapper.getPlayerView(video)?.getAttachedPlayer() ?: return false

        when (action) {
            START_COMMAND -> player.play()
            PAUSE_COMMAND -> player.pause()
            else -> {
                KAssert.fail { "No such video action: $action" }
                return false
            }
        }
        return true
    }

    private fun searchDivDataForVideo(divData: DivData, id: String, resolver: ExpressionResolver): DivVideo? {
        divData.states.forEach { state ->
            findDivVideoWithId(state.div.value(), id, resolver)?.let {
                return it
            }
        }
        return null
    }

    private fun findDivVideoWithId(div: DivBase, id: String, resolver: ExpressionResolver): DivVideo? {
        when (div) {
            is DivVideo -> return if (div.id == id) div else null
            is DivGallery -> {
                div.nonNullItems.forEach { item ->
                    findDivVideoWithId(item.value(), id, resolver)?.let {
                        return it
                    }
                }
                return null
            }
            is DivContainer -> {
                div.buildItems(resolver).forEach { (item, newResolver) ->
                    findDivVideoWithId(item.value(), id, newResolver)?.let {
                        return it
                    }
                }
                return null
            }
            is DivGrid -> {
                div.nonNullItems.forEach { item ->
                    findDivVideoWithId(item.value(), id, resolver)?.let {
                        return it
                    }
                }
                return null
            }
            is DivPager -> {
                div.nonNullItems.forEach { item ->
                    findDivVideoWithId(item.value(), id, resolver)?.let {
                        return it
                    }
                }
                return null
            }
            is DivTabs -> {
                div.items.forEach { item ->
                    findDivVideoWithId(item.div.value(), id, resolver)?.let {
                        return it
                    }
                }
                return null
            }
            is DivCustom -> {
                div.items?.forEach { item ->
                    findDivVideoWithId(item.value(), id, resolver)?.let {
                        return it
                    }
                }
                return null
            }
            is DivState -> {
                div.states.forEach { state ->
                    state.div?.value()?.let {  div ->
                        findDivVideoWithId(div, id, resolver)?.let {
                            return it
                        }
                    }
                }
                return null
            }
            else -> return null
        }
    }

    companion object {
        const val START_COMMAND = "start"
        const val PAUSE_COMMAND = "pause"
    }
}
