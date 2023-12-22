package com.yandex.div.core.view2.divs

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.variables.TwoWayIntegerVariableBinder
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerPlaybackConfig
import com.yandex.div.core.player.DivPlayerView
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
    private val divActionBinder: DivActionBinder,
    private val videoViewMapper: DivVideoViewMapper,
) : DivViewBinder<DivVideo, DivVideoView> {
    override fun bindView(view: DivVideoView, div: DivVideo, divView: Div2View) {
        val oldDiv = view.div
        val resolver = divView.expressionResolver

        val source = div.createSource(resolver)
        val config = DivPlayerPlaybackConfig(
            autoplay = div.autostart.evaluate(resolver),
            isMuted = div.muted.evaluate(resolver),
            repeatable = div.repeatable.evaluate(resolver),
            payload = div.playerSettingsPayload
        )

        val player = divView.div2Component.divVideoFactory.makePlayer(source, config)

        val currentPlayerView = view.getPlayerView()
        var currentPreviewView: ImageView? = null

        for (i in 0 until view.childCount) {
            val childView = view.getChildAt(i)
            if (childView is ImageView) {
                currentPreviewView = childView
                break
            }
        }

        val playerView = currentPlayerView ?: divView.div2Component.divVideoFactory.makePlayerView(view.context)

        val preview = div.createPreview(resolver)
        val previewImageView: ImageView = currentPreviewView ?: ImageView(view.context).apply {
            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            scaleType = ImageView.ScaleType.FIT_CENTER
            setBackgroundColor(Color.TRANSPARENT)
        }

        with(previewImageView) {
            if (preview != null) {
                visibility = View.VISIBLE
                setImageBitmap(preview)
            } else {
                visibility = View.INVISIBLE
            }
        }

        val playerListener = object : DivPlayer.Observer {
            override fun onPlay() {
                divActionBinder.handleActions(divView, div.resumeActions)
            }

            override fun onPause() {
                divActionBinder.handleActions(divView, div.pauseActions)
            }

            override fun onBuffering() {
                divActionBinder.handleActions(divView, div.bufferingActions)
            }

            override fun onEnd() {
                divActionBinder.handleActions(divView, div.endActions)
            }

            override fun onFatal() {
                divActionBinder.handleActions(divView, div.fatalActions)
            }

            override fun onReady() {
                previewImageView.visibility = View.INVISIBLE
            }
        }
        player.addObserver(playerListener)

        playerView.attach(player)

        if (div === oldDiv) {
            view.observeElapsedTime(div, divView, player)
            view.observeMuted(div, divView, player)
            view.observeScale(div, divView, playerView)
            return
        }

        view.observeElapsedTime(div, divView, player)
        view.observeMuted(div, divView, player)
        view.observeScale(div, divView, playerView)

        if (currentPreviewView == null && currentPlayerView == null) {
            view.removeAllViews()

            view.addView(playerView)
            view.addView(previewImageView)
        }

        videoViewMapper.addView(view, div)
        baseBinder.bindView(view, div, oldDiv, divView)
        view.observeAspectRatio(resolver, div.aspect)
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

    private fun DivVideoView.observeMuted(
        div: DivVideo,
        divView: Div2View,
        player: DivPlayer
    ) {
        addSubscription(
            div.muted.observeAndGet(divView.expressionResolver) {
                player.setMuted(it)
            }
        )
    }

    private fun DivVideoView.observeScale(
        div: DivVideo,
        divView: Div2View,
        playerView: DivPlayerView
    ) {
        addSubscription(
            div.scale.observeAndGet(divView.expressionResolver) {
                playerView.setScale(it)
            }
        )
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
