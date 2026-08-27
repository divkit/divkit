package com.yandex.div.core.player

import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.view2.Div2View
import com.yandex.div.internal.KAssert
import com.yandex.div.internal.core.DivTreeVisitor
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivData
import com.yandex.div2.DivVideo
import javax.inject.Inject

@DivScope
internal class DivVideoActionHandler @Inject constructor(
    private val videoViewMapper: DivVideoViewMapper
) {
    fun handleAction(
        div2View: Div2View,
        divId: String,
        action: String,
        expressionResolver: ExpressionResolver
    ): Boolean {
        val divData = div2View.divData ?: return false
        val video = searchDivDataForVideo(divData, divId, expressionResolver) ?: return false
        val player = videoViewMapper.getPlayer(video) ?: return false

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

    private fun searchDivDataForVideo(divData: DivData, id: String, resolver: ExpressionResolver) =
        DivVideoSearch(id).search(divData, resolver)

    private class DivVideoSearch(private val videoId: String) : DivTreeVisitor<DivVideo>({ it != null }) {

        fun search(data: DivData, resolver: ExpressionResolver) = visit(data, resolver)

        override fun defaultVisit(data: Div, resolver: ExpressionResolver, path: DivStatePath) = null

        override fun visit(data: Div.Video, resolver: ExpressionResolver, path: DivStatePath) =
            if (data.value.id == videoId) data.value else null
    }

    companion object {
        const val START_COMMAND = "start"
        const val PAUSE_COMMAND = "pause"
    }
}
