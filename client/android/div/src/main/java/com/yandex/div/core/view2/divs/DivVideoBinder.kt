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
import com.yandex.div.core.dagger.DivScope
import com.yandex.div.core.expression.variables.TwoWayIntegerVariableBinder
import com.yandex.div.core.player.DivPlayer
import com.yandex.div.core.player.DivPlayerFactory
import com.yandex.div.core.player.DivPlayerPlaybackConfig
import com.yandex.div.core.player.DivPlayerView
import com.yandex.div.core.player.DivVideoResolution
import com.yandex.div.core.player.DivVideoSource
import com.yandex.div.core.player.DivVideoViewMapper
import com.yandex.div.core.state.DivStatePath
import com.yandex.div.core.util.ImageRepresentation
import com.yandex.div.core.view2.BindingContext
import com.yandex.div.core.view2.DivViewBinder
import com.yandex.div.core.view2.divs.widgets.DivVideoView
import com.yandex.div.json.expressions.ExpressionResolver
import com.yandex.div2.Div
import com.yandex.div2.DivVideo
import com.yandex.div2.DivVideoScale
import java.util.concurrent.ExecutorService
import javax.inject.Inject
import com.yandex.div2.DivVideoSource as Div2VideoSource

@DivScope
internal class DivVideoBinder @Inject constructor(
    baseBinder: DivBaseBinder,
    private val variableBinder: TwoWayIntegerVariableBinder,
    private val divActionBinder: DivActionBinder,
    private val videoViewMapper: DivVideoViewMapper,
    private val executorService: ExecutorService,
    private val playerFactory: DivPlayerFactory,
) : DivViewBinder<Div.Video, DivVideo, DivVideoView>(baseBinder) {

    override fun DivVideoView.bind(
        bindingContext: BindingContext,
        div: DivVideo,
        oldDiv: DivVideo?,
        path: DivStatePath
    ) {
        this.path = path
        applyVideo(bindingContext, div, path)
        bindAspectRatio(div.aspect, oldDiv?.aspect, bindingContext.expressionResolver)
    }

    fun loadVideo(view: DivVideoView, bindingContext: BindingContext, div: DivVideo, path: DivStatePath) =
        view.applyVideo(bindingContext, div, path)

    private fun DivVideoView.applyVideo(
        bindingContext: BindingContext,
        div: DivVideo,
        path: DivStatePath,
    ) {
        val resolver = bindingContext.expressionResolver
        val source = div.createSource(resolver)
        val config = div.createConfig(resolver)

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
            // We won't to show black video square before preview is rendered
            visibility = View.INVISIBLE
        }

        val previewImageView: PreviewImageView = currentPreviewView ?: PreviewImageView(context)

        div.applyPreview(resolver) { preview ->
            preview?.let {
                with(previewImageView) {
                    visibility = View.VISIBLE
                    when (it) {
                        is ImageRepresentation.PictureDrawable -> setImageDrawable(it.value)
                        is ImageRepresentation.Bitmap -> setImageBitmap(it.value)
                    }
                }
            }
            playerView.visibility = View.VISIBLE
        }

        val player = playerFactory.makePlayer(source, config).apply {
            addObserver(createObserver(bindingContext, div, previewImageView))
            playerView.attach(this)
        }

        observeElapsedTime(div, bindingContext, player, path)
        observeMuted(div, resolver, player)
        observeScale(div, resolver, playerView, previewImageView)
        observeSource(div, resolver, player)

        if (currentPreviewView == null && currentPlayerView == null) {
            removeAllViews()

            addView(playerView)
            addView(previewImageView)
        }

        videoViewMapper.addView(this, div)
    }

    private fun createObserver(
        bindingContext: BindingContext,
        div: DivVideo,
        previewImageView: View
    ): DivPlayer.Observer {
        val divView = bindingContext.divView
        val resolver = bindingContext.expressionResolver
        return object : DivPlayer.Observer {
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
    }

    private fun DivVideoView.observeElapsedTime(
        div: DivVideo,
        bindingContext: BindingContext,
        player: DivPlayer,
        path: DivStatePath,
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

        addVideoSubscription(variableBinder.bindVariable(bindingContext, elapsedTimeVariable, callbacks, path))
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
        player: DivPlayer,
    ) {
        addVideoSubscription(
            div.observeSource(resolver) {
                player.setSource(it, div.createConfig(resolver))
            }
        )
    }

    private fun DivVideo.observeSource(
        resolver: ExpressionResolver,
        callback: (List<DivVideoSource>) -> Unit,
    ): Disposable {
        val itemCallback = { _ : Any -> callback(createSource(resolver)) }

        if (videoSources.size == 1) {
            return videoSources.first().observe(resolver, itemCallback)
        }

        val disposable = CompositeDisposable()
        videoSources.forEach {
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

    private fun DivVideo.applyPreview(
        resolver: ExpressionResolver,
        onPreviewDecoded: (ImageRepresentation?) -> Unit,
    ) {
        val base64String = preview?.evaluate(resolver)

        if (base64String == null) {
            onPreviewDecoded(null)
            return
        }

        val decodeTask = DecodeBase64ImageTask(base64String, false, onPreviewDecoded)
        executorService.submit(decodeTask)
    }

    private fun DivVideo.createConfig(
        resolver: ExpressionResolver,
    ) = DivPlayerPlaybackConfig(
        autoplay = autostart.evaluate(resolver),
        isMuted = muted.evaluate(resolver),
        repeatable = repeatable.evaluate(resolver),
        payload = playerSettingsPayload,
    )
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
