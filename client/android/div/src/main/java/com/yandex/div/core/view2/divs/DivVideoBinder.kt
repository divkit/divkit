package com.yandex.div.core.view2.divs

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.yandex.div.core.DecodeBase64ImageTask
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.variables.TwoWayIntegerVariableBinder
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerPlaybackConfig
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div.core.player.DivVideoResolution
import com.yandex.div.core.player.DivVideoSource
import com.yandex.div.core.player.DivVideoViewMapper
import com.yandex.div.core.util.ImageRepresentation
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivVideoView
import com.yandex.div.internal.KLog
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.DivVideo
import java.util.concurrent.ExecutorService
import javax.inject.Inject

@DivScope
internal class DivVideoBinder @Inject constructor(
    private val baseBinder: DivBaseBinder,
    private val variableBinder: TwoWayIntegerVariableBinder,
    private val divActionBinder: DivActionBinder,
    private val videoViewMapper: DivVideoViewMapper,
    private val executorService: ExecutorService,
) : DivViewBinder<DivVideo, DivVideoView> {
    override fun bindView(context: BindingContext, view: DivVideoView, div: DivVideo) {
        val oldDiv = view.div
        val divView = context.divView
        val resolver = context.expressionResolver

        baseBinder.bindView(context, view, div, oldDiv)

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

        val playerView = currentPlayerView ?: divView.div2Component.divVideoFactory.makePlayerView(view.context).apply {
            // We won't to show black video square before preview is rendered
            visibility = View.INVISIBLE
        }

        val previewImageView: ImageView = currentPreviewView ?: ImageView(view.context).apply {
            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            scaleType = ImageView.ScaleType.FIT_CENTER
            setBackgroundColor(Color.TRANSPARENT)
            visibility = View.INVISIBLE
        }

        div.applyPreview(resolver) { preview ->
            preview?.let {
                with(previewImageView) {
                    visibility = View.VISIBLE
                    when (it) {
                        is ImageRepresentation.PictureDrawable -> setImageDrawable(it.value)
                        is ImageRepresentation.Bitmap ->  setImageBitmap(it.value)
                    }
                }
            }
            playerView.visibility = View.VISIBLE
        }

        val playerListener = object : DivPlayer.Observer {
            override fun onPlay() {
                divActionBinder.handleActions(divView, resolver, div.resumeActions, DivActionReason.VIDEO)
            }

            override fun onPause() {
                divActionBinder.handleActions(divView, resolver, div.pauseActions, DivActionReason.VIDEO)
            }

            override fun onBuffering() {
                divActionBinder.handleActions(divView, resolver, div.bufferingActions, DivActionReason.VIDEO)
            }

            override fun onEnd() {
                divActionBinder.handleActions(divView, resolver, div.endActions, DivActionReason.VIDEO)
            }

            override fun onFatal() {
                divActionBinder.handleActions(divView, resolver, div.fatalActions, DivActionReason.VIDEO)
            }

            override fun onReady() {
                previewImageView.visibility = View.INVISIBLE
            }
        }
        player.addObserver(playerListener)

        playerView.attach(player)

        if (div === oldDiv) {
            view.observeElapsedTime(div, divView, player)
            view.observeMuted(div, resolver, player)
            view.observeScale(div, resolver, playerView)
            return
        }

        view.observeElapsedTime(div, divView, player)
        view.observeMuted(div, resolver, player)
        view.observeScale(div, resolver, playerView)

        if (currentPreviewView == null && currentPlayerView == null) {
            view.removeAllViews()

            view.addView(playerView)
            view.addView(previewImageView)
        }

        videoViewMapper.addView(view, div)
        view.bindAspectRatio(div.aspect, oldDiv?.aspect, resolver)
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
        resolver: ExpressionResolver,
        player: DivPlayer
    ) {
        addSubscription(
            div.muted.observeAndGet(resolver) {
                player.setMuted(it)
            }
        )
    }

    private fun DivVideoView.observeScale(
        div: DivVideo,
        resolver: ExpressionResolver,
        playerView: DivPlayerView
    ) {
        addSubscription(
            div.scale.observeAndGet(resolver) {
                playerView.setScale(it)
            }
        )
    }

    private fun DivVideo.applyPreview(
        resolver: ExpressionResolver,
        onPreviewDecoded:(ImageRepresentation?) -> Unit,
    ) {
        val base64String = preview?.evaluate(resolver)

        if (base64String == null) {
            onPreviewDecoded(null)
            return
        }

        val decodeTask = DecodeBase64ImageTask(base64String, false, onPreviewDecoded)
        executorService.submit(decodeTask)
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

@Deprecated("Will be removed in future releases")
fun DivVideo.createPreview(resolver: ExpressionResolver): Bitmap? {
    val base64String = preview?.evaluate(resolver) ?: return null
    val imageBytes = try {
        Base64.decode(base64String, Base64.DEFAULT)
    } catch (e: IllegalArgumentException) {
        KLog.d("Div") { "Bad base-64 image preview" }
        return null
    }
    return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
}
