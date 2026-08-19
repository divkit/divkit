package com.yandex.div.core.view2.divs

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import com.yandex.div.core.CompositeDisposable
import com.yandex.div.core.DecodeBase64ImageTask
import com.yandex.div.core.Disposable
import com.yandex.div.core.DivActionHandler.DivActionReason
import com.yandex.div.core.DivActionPerformer
import com.yandex.div.core.actions.logWarning
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.variables.TwoWayIntegerVariableBinder
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerFactory
import com.yandex.div.core.player.DivPlayerPlaybackConfig
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div.core.player.DivVideoPlaybackState
import com.yandex.div.core.player.DivVideoResolution
import com.yandex.div.core.player.DivVideoSource
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.ImageRepresentation
import com.yandex.div.core.view2.Div2View
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.DivVideoViewState
import com.yandex.div.core.view2.divs.widgets.DivVideoView
import com.yandex.div.core.view2.runMainThreadAction
import com.yandex.div.internal.core.DivBlock
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div.json.expressions.isConstant
import com.yandex.div2.DivVideo
import com.yandex.div2.DivVideoScale
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import com.yandex.div2.DivVideoSource as Div2VideoSource

@DivScope
internal class DivVideoBinder @Inject constructor(
    baseBinder: DivBaseBinder,
    private val variableBinder: TwoWayIntegerVariableBinder,
    private val actionPerformer: DivActionPerformer,
    private val executorService: ExecutorService,
    private val playerFactory: DivPlayerFactory,
) : DivViewBinder<DivBlock.Video, DivVideoView>(baseBinder) {

    override fun DivVideoView.bind(
        divBlock: DivBlock.Video,
        oldDivBlock: DivBlock.Video?,
        divView: Div2View,
    ) {
        applyVideo(divBlock.divValue, divBlock.expressionResolver, divBlock.path, divView)
        bindAspectRatio(divBlock.divValue.aspect, oldDivBlock?.divValue?.aspect, divBlock.expressionResolver)
    }

    fun loadVideo(view: DivVideoView, divBlock: DivBlock.Video, divView: Div2View) =
        view.applyVideo(divBlock.divValue, divBlock.expressionResolver, divBlock.path, divView)

    private fun DivVideoView.applyVideo(
        div: DivVideo,
        resolver: ExpressionResolver,
        path: DivStatePath,
        divView: Div2View,
    ) {
        val source = div.createSource(resolver)
        val config = div.createConfig(resolver, path, divView)
        val preview = div.preview?.evaluate(resolver)

        divView.runMainThreadAction {
            val currentSource = if (div.hasDynamicSource()) div.createSource(resolver) else source
            val currentConfig = if (div.hasDynamicConfig()) div.createConfig(resolver, path, divView) else config
            if (currentSource.isEmpty() && div.playerSettingsPayload == null) {
                divView.logSourceError(div)
            }

            val currentPlayerView = getPlayerView()
            var currentPreviewView: PreviewImageView? = null

            for (i in 0 until childCount) {
                val childView = getChildAt(i)
                if (childView is PreviewImageView) {
                    currentPreviewView = childView
                    break
                }
            }

            val playerView = currentPlayerView ?: playerFactory.makePlayerView(context).apply {
                // We won't show black video square before preview is rendered
                visibility = View.INVISIBLE
            }

            val previewImageView: PreviewImageView = currentPreviewView ?: PreviewImageView(context)

            applyPreview(preview) { decodedPreview ->
                decodedPreview?.let {
                    with(previewImageView) {
                        when (it) {
                            is ImageRepresentation.PictureDrawable -> setImageDrawable(it.value)
                            is ImageRepresentation.Bitmap -> setImageBitmap(it.value)
                            is ImageRepresentation.Error -> {
                                divView.logWarning(it.value)
                                return@let
                            }
                        }
                        visibility = View.VISIBLE
                    }
                }
                playerView.visibility = View.VISIBLE
            }

            val player = playerFactory.makePlayer(currentSource, currentConfig).apply {
                addObserver(createObserver(div, resolver, divView, previewImageView))
                playerView.attach(this)
            }

            observeElapsedTime(div, resolver, divView, player)
            observeMuted(div, resolver, player)
            observePlaybackSpeed(div, resolver, player)
            observePlaybackState(path, divView, player)
            observeScale(div, resolver, playerView, previewImageView)
            observeSource(div, resolver, path, player, divView)

            if (currentPreviewView == null && currentPlayerView == null) {
                removeAllViews()

                addView(playerView)
                addView(previewImageView)
            }
        }
    }

    private fun createObserver(
        div: DivVideo,
        resolver: ExpressionResolver,
        divView: Div2View,
        previewImageView: View
    ): DivPlayer.Observer {
        return object : DivPlayer.Observer {
            override fun onPlay() {
                actionPerformer.performActions(divView, resolver, div.resumeActions, DivActionReason.VIDEO)
            }

            override fun onPause() {
                actionPerformer.performActions(divView, resolver, div.pauseActions, DivActionReason.VIDEO)
            }

            override fun onBuffering() {
                actionPerformer.performActions(divView, resolver, div.bufferingActions, DivActionReason.VIDEO)
            }

            override fun onEnd() {
                actionPerformer.performActions(divView, resolver, div.endActions, DivActionReason.VIDEO)
            }

            override fun onFatal() {
                actionPerformer.performActions(divView, resolver, div.fatalActions, DivActionReason.VIDEO)
            }

            override fun onFatal(error: Throwable) {
                divView.logError(RuntimeException("Playback in div with id '${div.id}' encountered an error:", error))
                onFatal()
            }

            override fun onReady() {
                previewImageView.visibility = View.INVISIBLE
            }
        }
    }

    private fun DivVideoView.observeElapsedTime(
        div: DivVideo,
        resolver: ExpressionResolver,
        divView: Div2View,
        player: DivPlayer,
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

        addVideoSubscription(variableBinder.bindVariable(elapsedTimeVariable, resolver, divView, callbacks))
    }

    private fun DivVideoView.observeMuted(
        div: DivVideo,
        resolver: ExpressionResolver,
        player: DivPlayer
    ) {
        addVideoSubscription(
            div.muted.observeAndGet(resolver) {
                player.setMuted(it)
            }
        )
    }

    private fun DivVideoView.observePlaybackSpeed(
        div: DivVideo,
        resolver: ExpressionResolver,
        player: DivPlayer,
    ) {
        addVideoSubscription(
            div.playbackSpeed.observeAndGet(resolver) {
                player.setPlaybackSpeed(it.toFloat())
            }
        )
    }

    private fun DivVideoView.observePlaybackState(
        path: DivStatePath,
        divView: Div2View,
        player: DivPlayer,
    ) {
        addVideoSubscription(
            divView.viewStateStore.observe(path.fullPath) { state ->
                val playbackState = (state as? DivVideoViewState)?.playbackState ?: return@observe
                when (playbackState) {
                    DivVideoPlaybackState.PLAYING -> player.play()
                    DivVideoPlaybackState.PAUSED -> player.pause()
                }
            }
        )
    }

    private fun DivVideoView.observeScale(
        div: DivVideo,
        resolver: ExpressionResolver,
        playerView: DivPlayerView,
        previewView: PreviewImageView,
    ) {
        addVideoSubscription(
            div.scale.observeAndGet(resolver) {
                playerView.setScale(it)
                previewView.setScale(it)
            }
        )
    }

    private fun DivVideoView.observeSource(
        div: DivVideo,
        resolver: ExpressionResolver,
        path: DivStatePath,
        player: DivPlayer,
        divView: Div2View,
    ) {
        addVideoSubscription(
            div.observeSource(resolver) {
                if (it.isEmpty() && div.playerSettingsPayload == null) {
                    divView.logSourceError(div)
                }
                player.setSource(it, div.createConfig(resolver, path, divView))
            }
        )
        div.playerSettingsPayload?.let { payload ->
            addVideoSubscription(
                payload.observe(resolver) {
                    player.setSource(div.createSource(resolver), div.createConfig(resolver, path, divView))
                }
            )
        }
    }

    private fun DivVideo.observeSource(
        resolver: ExpressionResolver,
        callback: (List<DivVideoSource>) -> Unit,
    ): Disposable {
        val itemCallback = { _ : Any -> callback(createSource(resolver)) }

        val sources = videoSources ?: return Disposable.NULL
        if (sources.isEmpty()) return Disposable.NULL

        if (sources.size == 1) {
            return sources.first().observe(resolver, itemCallback)
        }

        val disposable = CompositeDisposable()
        sources.forEach {
            disposable.add(it.observe(resolver, itemCallback))
        }

        return disposable
    }

    private fun Div2VideoSource.observe(
        resolver: ExpressionResolver,
        callback: (Any) -> Unit,
    ): Disposable {
        val disposable = CompositeDisposable()

        bitrate?.let { disposable.add(it.observe(resolver, callback)) }
        disposable.add(mimeType.observe(resolver, callback))
        resolution?.let {
            disposable.add(it.height.observe(resolver, callback))
            disposable.add(it.width.observe(resolver, callback))
        }
        disposable.add(url.observe(resolver, callback))

        return disposable
    }

    private fun applyPreview(
        base64String: String?,
        onPreviewDecoded: (ImageRepresentation?) -> Unit,
    ) {
        if (base64String == null) {
            onPreviewDecoded(null)
            return
        }

        val decodeTask = DecodeBase64ImageTask(base64String, false, onPreviewDecoded)
        executorService.submit(decodeTask)
    }

    private fun DivVideo.createConfig(
        resolver: ExpressionResolver,
        path: DivStatePath,
        divView: Div2View,
    ): DivPlayerPlaybackConfig {
        val defaultPlaybackState =
            if (autostart.evaluate(resolver)) DivVideoPlaybackState.PLAYING else DivVideoPlaybackState.PAUSED
        val playbackState = divView.viewStateStore.getOrPut(path.fullPath) {
            DivVideoViewState(defaultPlaybackState)
        } as? DivVideoViewState

        return DivPlayerPlaybackConfig(
            autoplay = (playbackState?.playbackState ?: defaultPlaybackState) == DivVideoPlaybackState.PLAYING,
            isMuted = muted.evaluate(resolver),
            repeatable = repeatable.evaluate(resolver),
            payload = playerSettingsPayload?.evaluate(resolver),
            playbackSpeed = playbackSpeed.evaluate(resolver).toFloat(),
        )
    }

    private fun DivVideo.hasDynamicSource(): Boolean {
        return videoSources?.any { source ->
            !source.url.isConstant() ||
                !source.mimeType.isConstant() ||
                source.bitrate?.isConstant() == false ||
                source.resolution?.let { !it.width.isConstant() || !it.height.isConstant() } == true
        } == true
    }

    private fun DivVideo.hasDynamicConfig(): Boolean {
        return !autostart.isConstant() ||
            !muted.isConstant() ||
            !repeatable.isConstant() ||
            playerSettingsPayload?.isConstant() == false ||
            !playbackSpeed.isConstant()
    }

    private fun Div2View.logSourceError(div: DivVideo) {
        logError(Throwable(
            "Neither 'video_source' nor 'player_settings_payload' are specified for video with id '${div.id}'"
        ))
    }
}

fun DivVideo.createSource(resolver: ExpressionResolver): List<DivVideoSource> {
    return videoSources?.map {
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
    } ?: emptyList()
}

private class PreviewImageView(context: Context) : AppCompatImageView(context) {

    init {
        layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        setBackgroundColor(Color.TRANSPARENT)
        visibility = INVISIBLE
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable?.tryScaleAccordingToDensity())
    }

    override fun setImageBitmap(bm: Bitmap?) {
        if (scaleType == NO_SCALE) {
            bm?.density = DisplayMetrics.DENSITY_DEFAULT
        }
        super.setImageBitmap(bm)
    }

    fun setScale(scale: DivVideoScale) {
        val previewScale = when(scale) {
            DivVideoScale.FILL -> FILL
            DivVideoScale.NO_SCALE -> NO_SCALE
            DivVideoScale.FIT -> FIT
        }
        scaleType = previewScale
    }

    private fun Drawable.tryScaleAccordingToDensity(): Drawable = when {
        scaleType != NO_SCALE -> this

        this is BitmapDrawable -> this.apply {
            bitmap?.density = DisplayMetrics.DENSITY_DEFAULT
            setTargetDensity(context.resources.displayMetrics)
        }

        else -> this
    }

    companion object {
        private val NO_SCALE = ScaleType.CENTER
        private val FIT = ScaleType.FIT_CENTER
        private val FILL = ScaleType.CENTER_CROP
    }
}
