package com.yandex.div.core.view2.divs

import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.variables.TwoWayIntegerVariableBinder
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerPlaybackConfig
import com.yandex.div.core.player.DivVideoPauseReason
import com.yandex.div.core.player.DivVideoResolution
import com.yandex.div.core.player.DivVideoSource
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivVideoView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivVideo
import com.yandex.div2.DivVideoData
import javax.inject.Inject

@DivScope
internal class DivVideoBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val variableBinder: TwoWayIntegerVariableBinder,
    private val divActionHandler: DivActionHandler,
) : DivViewBinder<DivVideo, DivVideoView> {
    override fun bindView(view: DivVideoView, div: DivVideo, divView: Div2View) {
        val oldDiv = view.div
        if (div == oldDiv) return

        val resolver = divView.expressionResolver
        view.closeAllSubscription()

        view.div = div
        if (oldDiv != null) baseBinder.unbindExtensions(view, oldDiv, divView)

        view.removeAllViews()

        val player = divView.div2Component.divVideoFactory.makePlayer(
            div.createSource(resolver),
            DivPlayerPlaybackConfig(
                autoplay = div.autostart.evaluate(resolver),
                isMuted = div.muted.evaluate(resolver),
                repeatable = div.repeatable.evaluate(resolver),
                payload = div.playerSettingsPayload
            )
        )

        val playerView = divView.div2Component.divVideoFactory.makePlayerView(view.context)
        view.addView(playerView)

        playerView.attach(player)

        baseBinder.bindView(view, div, oldDiv, divView)

        val playerListener = object : DivPlayer.Observer {
            override fun onResume() {
                div.resumeActions?.forEach { divAction ->
                    divView.let { divActionHandler.handleAction(divAction, it) }
                }
            }

            override fun onPause(reason: DivVideoPauseReason) {
                when (reason) {
                    DivVideoPauseReason.BUFFER_OVER -> {
                        div.bufferingActions?.forEach { divAction ->
                            divView.let { divActionHandler.handleAction(divAction, it) }
                        }
                    }
                    DivVideoPauseReason.VIDEO_OVER -> {
                        div.endActions?.forEach { divAction ->
                            divView.let { divActionHandler.handleAction(divAction, it) }
                        }
                    }
                    else -> Unit
                }
            }
        }
        player.addObserver(playerListener)

        view.apply {
            observeElapsedTime(div, divView, player)
        }
    }

    private fun DivVideoView.observeElapsedTime(
            div: DivVideo,
            divView: Div2View,
            player: DivPlayer
    ) {
        val elapsedTimeVariable = div.elapsedTimeVariable ?: return

        val callbacks = object : TwoWayIntegerVariableBinder.Callbacks {
            override fun onVariableChanged(value: Long?) {
                value?.let {
                    player.seek(value)
                }
            }

            override fun setViewStateChangeListener(valueUpdater: (Long) -> Unit) {
                player.addObserver(object : DivPlayer.Observer {
                    override fun onCurrentTimeUpdate(timeMs: Long) {
                        valueUpdater(timeMs)
                    }
                })
            }
        }

        addSubscription(variableBinder.bindVariable(divView, elapsedTimeVariable, callbacks))
    }
}

fun DivVideo.createSource(resolver: ExpressionResolver): List<DivVideoSource> {
    return when (this.videoData) {
        is DivVideoData.Video -> {
            (this.videoData as DivVideoData.Video).value.videoSources.map {
                DivVideoSource.FileVideoSource(
                    url = it.url.evaluate(resolver),
                    codec = it.codec?.evaluate(resolver),
                    mimeType = it.mimeType?.evaluate(resolver),
                    resolution = it.resolution?.let { resolution ->
                        DivVideoResolution(
                                resolution.width.evaluate(resolver).toInt(),
                                resolution.height.evaluate(resolver).toInt()
                        )
                    }
                )
            }
        }
        is DivVideoData.Stream -> {
            listOf(
                DivVideoSource.StreamVideoSource(
                    url = (this.videoData as DivVideoData.Stream).value.url.evaluate(resolver)
                )
            )
        }
    }
}
