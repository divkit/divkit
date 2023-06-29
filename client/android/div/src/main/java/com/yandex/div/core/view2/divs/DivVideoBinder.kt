package com.yandex.div.core.view2.divs

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.yandex.div.core.DivActionHandler
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.variables.TwoWayIntegerVariableBinder
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerPlaybackConfig
import com.yandex.div.core.player.DivVideoResolution
import com.yandex.div.core.player.DivVideoSource
import com.yandex.div.core.player.DivVideoViewMapper
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivVideoView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivVideo
import javax.inject.Inject

@DivScope
internal class DivVideoBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val variableBinder: TwoWayIntegerVariableBinder,
    private val divActionHandler: DivActionHandler,
    private val videoViewMapper: DivVideoViewMapper
) : DivViewBinder<DivVideo, DivVideoView> {
    override fun bindView(view: DivVideoView, div: DivVideo, divView: Div2View) {
        val resolver = divView.expressionResolver
        val player = divView.div2Component.divVideoFactory.makePlayer(
            div.createSource(resolver),
            DivPlayerPlaybackConfig(
                autoplay = div.autostart.evaluate(resolver),
                isMuted = div.muted.evaluate(resolver),
                repeatable = div.repeatable.evaluate(resolver),
                payload = div.playerSettingsPayload
            )
        )

        val oldDiv = view.div
        if (div == oldDiv) {
            // Should recreate player when rebinding view because player engine was released
            view.getPlayerView()?.attach(player)
            return
        }

        view.closeAllSubscription()

        view.div = div
        if (oldDiv != null) baseBinder.unbindExtensions(view, oldDiv, divView)

        view.removeAllViews()

        val playerView = divView.div2Component.divVideoFactory.makePlayerView(view.context)
        val preview = div.createPreview(resolver)
        val previewImageView: ImageView = ImageView(view.context).apply {
            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            scaleType = ImageView.ScaleType.FIT_CENTER
            if (preview != null) {
                visibility = View.VISIBLE
                setImageBitmap(preview)
            } else {
                visibility = View.INVISIBLE
            }
            setBackgroundColor(Color.TRANSPARENT)
        }
        view.addView(playerView)
        view.addView(previewImageView)

        playerView.attach(player)

        videoViewMapper.addView(view, div)
        baseBinder.bindView(view, div, oldDiv, divView)

        val playerListener = object : DivPlayer.Observer {
            override fun onPlay() {
                div.resumeActions?.forEach { divAction ->
                    divView.let { divActionHandler.handleAction(divAction, it) }
                }
            }

            override fun onBuffering() {
                div.bufferingActions?.forEach { divAction ->
                    divView.let { divActionHandler.handleAction(divAction, it) }
                }
            }

            override fun onEnd() {
                div.endActions?.forEach { divAction ->
                    divView.let { divActionHandler.handleAction(divAction, it) }
                }
            }

            override fun onReady() {
                previewImageView.visibility = View.INVISIBLE
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
                    override fun onCurrentTimeChange(timeMs: Long) {
                        valueUpdater(timeMs)
                    }
                })
            }
        }

        addSubscription(variableBinder.bindVariable(divView, elapsedTimeVariable, callbacks))
    }
}

fun DivVideo.createSource(resolver: ExpressionResolver): List<DivVideoSource> {
    return videoSources.map {
        DivVideoSource(
            url = it.url.evaluate(resolver),
            mimeType = it.mimeType.evaluate(resolver),
            resolution = it.resolution?.let { resolution ->
                DivVideoResolution(
                    resolution.width.evaluate(resolver).toInt(),
                    resolution.height.evaluate(resolver).toInt()
                )
            },
            bitrate = it.bitrate?.evaluate(resolver)
        )
    }
}

fun DivVideo.createPreview(resolver: ExpressionResolver): Bitmap? {
    val base64String = preview?.evaluate(resolver) ?: return null
    val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}
